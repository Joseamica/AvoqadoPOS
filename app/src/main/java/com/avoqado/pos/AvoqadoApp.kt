package com.avoqado.pos

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.util.Log
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
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.menta.android.restclient.core.Storage
import timber.log.Timber

class AvoqadoApp : Application() {
    companion object {
        lateinit var storage: Storage
        lateinit var sessionManager: SessionManager
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
        val terminalRepository: TerminalRepository by lazy {
            TerminalRepositoryImpl(
                sessionManager = sessionManager,
                mentaService = AvoqadoAPI.mentaService,
                avoqadoService = AvoqadoAPI.apiService,
                storage = storage,
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
                Log.d("AvoqadoApp", "Socket service connected")
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                socketService = null
                socketServiceBound = false
                Log.d("AvoqadoApp", "Socket service disconnected")
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        Timber.plant(
            FirebasePlant(),
        )

        // Initialize AppConfig first so other components can use it
        AppConfig.initialize(this)

        storage = Storage(this)
        sessionManager = SessionManager(this)
        terminalSerialCode =
            when {
                // For Android 8+ (API 26+)
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                    // ANDROID_ID is consistent per app install across all Android versions
                    Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
                }
                // Fallback for older versions (shouldn't be needed given your minSdk)
                else -> {
                    val uuid =
                        java.util.UUID
                            .randomUUID()
                            .toString()
                    // Store this UUID persistently if needed
                    uuid
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
        
        Log.d("AvoqadoApp", "Started and binding to SocketService")
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
