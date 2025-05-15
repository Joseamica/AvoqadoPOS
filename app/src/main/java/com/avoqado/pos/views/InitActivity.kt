package com.avoqado.pos.views

import android.content.Intent
import android.os.Bundle
import timber.log.Timber
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.res.stringResource
import com.avoqado.pos.ACQUIRER_NAME
import com.avoqado.pos.AppfinRestClientConfigure
import com.avoqado.pos.COUNTRY_CODE
import com.avoqado.pos.MainActivity
import com.avoqado.pos.R
import com.avoqado.pos.merchantApiKey
import com.avoqado.pos.merchantId
import com.avoqado.pos.ui.screen.ProcessingOperationScreen
import com.menta.android.common_cross.util.StatusType
import com.menta.android.core.viewmodel.ExternalTokenData
import com.menta.android.core.viewmodel.MasterKeyData
import com.menta.android.restclient.core.RestClientConfiguration.configure
import com.menta.android.restclient.core.Storage

class InitActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.i("InitActivity started") // Log al inicio de la actividad
        
        // Comprobar si estamos reiniciando la app completamente
        val isCompleteRestart = intent.getBooleanExtra("RESTART_COMPLETE", false)
        
        if (isCompleteRestart) {
            Timber.i("Detected RESTART_COMPLETE flag, redirecting to MainActivity")
            // Si es un reinicio completo, ir directamente a MainActivity (SplashScreen)
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            return
        }
        
        enableEdgeToEdge()
        setContent {
            ProcessingOperationScreen(
                title = stringResource(id = R.string.wait_payment),
                message = stringResource(id = R.string.whileInitProcessFinishes),
            )
        }
        configure(AppfinRestClientConfigure())
        val externalTokenData = ExternalTokenData(this)
        Timber.i("Requesting token with API key: $merchantApiKey") // Log antes de solicitar el token

        // Recuperar el token. TODO La apiKey es la que se generó previamente por el cliente, es una por merchant
        externalTokenData.getExternalToken(merchantApiKey)

        // Observer
        externalTokenData.getExternalToken.observe(this) { token ->
            Timber.i("Token received: $token")

            if (token.status.statusType != StatusType.ERROR) {
                Timber.i("Get token SUCCESS")
                // Guardar el token
                val storage = Storage(this)
                storage.putIdToken(token.idToken)
                storage.putTokenType(token.tokenType)

                // Inyección de llaves
                val masterKeyData = MasterKeyData(this)
                masterKeyData.loadMasterKey(
                    merchantId = merchantId, // TODO el cliente debe conocer su merchantId, es diferente por cada comercio que tenga
                    acquirerId = ACQUIRER_NAME,
                    countryCode = COUNTRY_CODE,
                )
                masterKeyData.getMasterKey.observe(this) { keyResult ->
                    keyResult?.secretsList?.let {
                        goToContinue()
                    } ?: run {
                        goToFailedScreen()
                    }
                }
            } else {
                Timber.i("Get token ERROR: ${token.status.message}")
            }
        }
    }

    private fun goToContinue() {
        Timber.i("Inyección de llaves SUCCESS")
        
        // Comprobar si estamos reiniciando la app completamente
        val isCompleteRestart = intent.getBooleanExtra("RESTART_COMPLETE", false)
        
        if (isCompleteRestart) {
            // Si es un reinicio completo, volver a MainActivity para pasar por SplashScreen
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        } else {
            // Flujo normal
            Intent(this, MenuActivity::class.java)
                .let(::startActivity)
        }
    }

    private fun goToFailedScreen() {
        Timber.i("Inyección de llaves ERROR")
    }
}
