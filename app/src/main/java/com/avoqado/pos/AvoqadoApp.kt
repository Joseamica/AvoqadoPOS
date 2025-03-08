package com.avoqado.pos

import android.app.Application
import android.os.Build
import android.provider.Settings
import com.avoqado.pos.core.data.local.SessionManager
import com.avoqado.pos.core.data.network.AvoqadoAPI
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
        val managementRepository: ManagementRepository by lazy { ManagementRepositoryImpl(managementCacheStorage = managementCacheStorage) }
        val PaymentCacheStorage: PaymentCacheStorage by lazy { PaymentCacheStorage() }
        val paymentRepository: PaymentRepository by lazy {
            PaymentRepositoryImpl(
                paymentCacheStorage = PaymentCacheStorage,
                avoqadoService = AvoqadoAPI.retrofit.create(AvoqadoService::class.java)
            )
        }
        val terminalRepository : TerminalRepository by lazy {
            TerminalRepositoryImpl(
                sessionManager = sessionManager,
                mentaService = AvoqadoAPI.mentaService ,
                storage = storage
            )
        }
        val authorizationRepository: AuthorizationRepository by lazy {
            AuthorizationRepositoryImpl(
                sessionManager = sessionManager
            )
        }
    }

    override fun onCreate() {
        super.onCreate()
        Timber.plant(
            FirebasePlant()
        )

        storage = Storage(this)
        sessionManager = SessionManager(this)
        terminalSerialCode =  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        } else {
            Build.SERIAL ?: "Unknown"
        }
    }

    class FirebasePlant() : Timber.Tree() {
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            FirebaseCrashlytics.getInstance().log("$tag: $message")
            t?.let {
                FirebaseCrashlytics.getInstance().recordException(it)
            }
        }
    }
}