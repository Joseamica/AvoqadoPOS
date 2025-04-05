package com.avoqado.pos


import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.avoqado.pos.core.presentation.delegates.SnackbarDelegate
import com.avoqado.pos.core.presentation.navigation.NavigationDispatcher
import com.avoqado.pos.core.presentation.navigation.NavigationManager
import com.avoqado.pos.core.presentation.navigation.NavigationManagerImpl
import com.avoqado.pos.core.presentation.router.AppRouter
import com.avoqado.pos.core.presentation.theme.AvoqadoTheme
import com.menta.android.core.viewmodel.ExternalTokenData
import com.menta.android.core.viewmodel.MasterKeyData
import com.menta.android.core.viewmodel.TrxData
import com.menta.android.restclient.core.RestClientConfiguration.configure

class MainActivity : ComponentActivity() {
    private val snackbarDelegate: SnackbarDelegate = SnackbarDelegate()
    private val navigationManager: NavigationManager = NavigationManagerImpl()
    private val  navigationDispatcher: NavigationDispatcher by lazy {
        NavigationDispatcher(navigationManager)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        configure(AppfinRestClientConfigure())
        // Obtener el serial number y mostrarlo en el log
        val serialNumber = getDeviceSerialNumber()
        Log.d("MainActivity", "Device Serial Number: $serialNumber")
        getScreenInfo()

        val externalTokenData = ExternalTokenData(this)
        val masterKeyData = MasterKeyData(this)
        val trxData = TrxData(this)

        setContent {
            AvoqadoTheme {
                AppRouter(
                    navigationDispatcher = navigationDispatcher,
                    snackbarDelegate = snackbarDelegate,
                    externalTokenData = externalTokenData,
                    masterKeyData = masterKeyData,
                    trxData = trxData,
                    context = this
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intent.getStringExtra("navigate_to")?.let { destination ->
            navigationDispatcher.navigateTo(destination)
        }
    }


    override fun onBackPressed() {
    }

    private fun getDeviceSerialNumber(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        } else {
            Build.SERIAL ?: "Unknown"
        }
    }

    fun getScreenInfo() {
        val displayMetrics = DisplayMetrics()

        @Suppress("DEPRECATION") // For older APIs
        this.windowManager.defaultDisplay.getMetrics(displayMetrics)

        val densityDpi = displayMetrics.densityDpi
        val widthPx = displayMetrics.widthPixels
        val heightPx = displayMetrics.heightPixels
        val density = displayMetrics.density
        val scaledDensity = displayMetrics.scaledDensity

        Log.d("AvoqadoSettings","Screen Width: ${widthPx}px")
        Log.d("AvoqadoSettings","Screen Height: ${heightPx}px")
        Log.d("AvoqadoSettings","Density: $density")
        Log.d("AvoqadoSettings","Density DPI: $densityDpi dpi")
        Log.d("AvoqadoSettings","Scaled Density: $scaledDensity")
    }
}
