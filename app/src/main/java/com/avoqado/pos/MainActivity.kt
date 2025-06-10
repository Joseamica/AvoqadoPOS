package com.avoqado.pos

import android.content.ComponentCallbacks2
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.DisplayMetrics
import timber.log.Timber
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import com.avoqado.pos.core.presentation.delegates.SnackbarDelegate
import com.avoqado.pos.core.presentation.navigation.NavigationDispatcher
import com.avoqado.pos.core.presentation.navigation.NavigationManager
import com.avoqado.pos.core.presentation.navigation.NavigationManagerImpl
import com.avoqado.pos.core.presentation.router.AppRouter
import com.avoqado.pos.core.presentation.theme.AvoqadoTheme

class MainActivity : ComponentActivity() {
    private val snackbarDelegate: SnackbarDelegate = SnackbarDelegate()
    private val navigationManager: NavigationManager = NavigationManagerImpl()
    private val navigationDispatcher: NavigationDispatcher by lazy {
        NavigationDispatcher(navigationManager)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Reducir el tiempo de renderizado al inicio
        window.setFlags(
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
        )

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Optimizar para dispositivos de baja RAM
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        }

        // Habilitar decoración de sistema por debajo de la UI
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val serialNumber = getDeviceSerialNumber()
        Timber.d("Device Serial Number: $serialNumber")
        getScreenInfo()


        setContent {
            AvoqadoTheme {
                AppRouter(
                    navigationDispatcher = navigationDispatcher,
                    snackbarDelegate = snackbarDelegate,
                    context = this,
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
        // No permitir navegación hacia atrás con botón físico
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        // Liberar recursos cuando el sistema reporta baja memoria
        if (level >= ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW) {
            System.gc()
        }
    }

    private fun getDeviceSerialNumber(): String =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        } else {
            Build.SERIAL ?: "Unknown"
        }

    private fun getScreenInfo() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val metrics = windowManager.currentWindowMetrics
            // Use metrics.bounds to get size information
            val widthPx = metrics.bounds.width()
            val heightPx = metrics.bounds.height()
            // Get density from resources
            val density = resources.displayMetrics.density
            val densityDpi = resources.displayMetrics.densityDpi

            Timber.d("Screen Width: ${widthPx}px")
            Timber.d("Screen Height: ${heightPx}px")
        } else {
            val displayMetrics = DisplayMetrics()

            @Suppress("DEPRECATION") // For older APIs
            this.windowManager.defaultDisplay.getMetrics(displayMetrics)

            val densityDpi = displayMetrics.densityDpi
            val widthPx = displayMetrics.widthPixels
            val heightPx = displayMetrics.heightPixels
            val density = displayMetrics.density
            val scaledDensity = displayMetrics.scaledDensity

            Timber.d("AvoqadoSettings", "Screen Width: ${widthPx}px")
            Timber.d("Screen Height: ${heightPx}px")
            Timber.d("Density: $density")
            Timber.d("Density DPI: $densityDpi dpi")
            Timber.d("Scaled Density: $scaledDensity")
        }
    }
}
