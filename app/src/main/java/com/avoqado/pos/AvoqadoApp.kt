package com.avoqado.pos


import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.Manifest

import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import com.avoqado.pos.core.data.local.SessionManager
import com.avoqado.pos.core.data.network.AvoqadoAPI
import com.avoqado.pos.core.data.network.AppConfig
import com.avoqado.pos.core.data.network.SocketService
import com.avoqado.pos.core.data.repository.TerminalRepositoryImpl
import com.avoqado.pos.core.domain.repositories.TerminalRepository
import com.avoqado.pos.features.authorization.data.AuthorizationRepositoryImpl
import com.avoqado.pos.features.authorization.domain.AuthorizationRepository
import com.avoqado.pos.features.management.data.ManagementRepositoryImpl
import com.avoqado.pos.features.management.data.cache.ManagementCacheStorage
import com.avoqado.pos.features.management.domain.ManagementRepository
import com.avoqado.pos.features.payment.data.PaymentRepositoryImpl
import com.avoqado.pos.features.payment.data.cache.PaymentCacheStorage
import com.avoqado.pos.features.payment.data.network.AvoqadoService
import com.avoqado.pos.features.payment.domain.repository.PaymentRepository
import com.avoqado.pos.features.menu.data.repository.MenuRepositoryImpl
import com.avoqado.pos.features.menu.domain.repository.MenuRepository
import com.example.content_core_service.ContentService
import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber

class AvoqadoApp : Application() {
    companion object {
        lateinit var sessionManager: SessionManager
        lateinit var contentService: ContentService
        var terminalSerialCode: String = ""
        val managementCacheStorage: ManagementCacheStorage by lazy { ManagementCacheStorage() }
        val managementRepository: ManagementRepository by lazy {
            ManagementRepositoryImpl(
                managementCacheStorage = managementCacheStorage,
                avoqadoService = AvoqadoAPI.apiService,
            )
        }
        val PaymentCacheStorage: PaymentCacheStorage by lazy { PaymentCacheStorage() }
        val paymentRepository: PaymentRepository by lazy {
            PaymentRepositoryImpl(
                paymentCacheStorage = PaymentCacheStorage,
                avoqadoService = AvoqadoAPI.retrofit.create(AvoqadoService::class.java),
            )
        }
        
        val menuRepository: MenuRepository by lazy {
            MenuRepositoryImpl(
                avoqadoService = AvoqadoAPI.apiService
            )
        }
        val terminalRepository: TerminalRepository by lazy {
            TerminalRepositoryImpl(
                sessionManager = sessionManager,
                mentaService = AvoqadoAPI.mentaService,
                avoqadoService = AvoqadoAPI.apiService,
                socketIOManager = AvoqadoAPI.socketIOManager
            )
        }
        val authorizationRepository: AuthorizationRepository by lazy {
            AuthorizationRepositoryImpl(
                sessionManager = sessionManager,
                avoqadoService = AvoqadoAPI.apiService,
            )
        }
        
        // Add a reference to the socket service
        var socketService: SocketService? = null
        private var socketServiceBound = false
        
        // Service connection for binding to the SocketService
        private val socketServiceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val binder = service as SocketService.SocketBinder
                socketService = binder.getService()
                socketServiceBound = true
                Timber.tag("AvoqadoApp").d("Socket service connected")
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                socketService = null
                socketServiceBound = false
                Timber.tag("AvoqadoApp").d("Socket service disconnected")
            }
        }
    }

    override fun onCreate() {
        super.onCreate()

        // Plant Firebase tree for production crash reporting
        Timber.plant(FirebasePlant())
        val isDebugBuild = applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE != 0

        // Add a DebugTree in debug builds to show logs in Logcat
        if (isDebugBuild) {
            Timber.plant(Timber.DebugTree())
        }
        // Initialize AppConfig first so other components can use it
        AppConfig.initialize(this)
        contentService = ContentService(this)
        sessionManager = SessionManager(this)
        terminalSerialCode = when {
            // For Android 10+ (API 29+) - This covers your Android 12 production environment
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
            }
            // For Android 8.0 to Android 9 (API 26-28) - This covers your Android 8.1 development environment
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                try {
                    if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                        Build.getSerial()
                    } else {
                        // Fallback if permission not granted
                        Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
                    }
                } catch (e: Exception) {
                    Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
                }
            }
            // For older versions (not needed in your case but included for completeness)
            else -> {
                Build.SERIAL ?: "Unknown"
            }
        }
            
        // Start and bind to the Socket service
        startAndBindSocketService()
    }
    
    private fun startAndBindSocketService() {
        // Create an intent to start the service
        val intent = Intent(this, SocketService::class.java)
        
        // Start the service
        startService(intent)
        
        // Bind to the service
        bindService(intent, socketServiceConnection, Context.BIND_AUTO_CREATE)

        Timber.d("Started and binding to SocketService")
    }
    
    override fun onTerminate() {
        // Unbind from the service when the application is terminated
        if (socketServiceBound) {
            unbindService(socketServiceConnection)
            socketServiceBound = false
        }
        super.onTerminate()
    }

    class FirebasePlant : Timber.Tree() {
        override fun log(
            priority: Int,
            tag: String?,
            message: String,
            t: Throwable?,
        ) {
            super.log(priority, tag, message, t)
            FirebaseCrashlytics.getInstance().log("$tag: $message")
            t?.let {
                FirebaseCrashlytics.getInstance().recordException(it)
            }
        }
    }
}
