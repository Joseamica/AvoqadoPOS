package com.avoqado.pos


import android.os.Build
import android.provider.Settings
import android.util.Log
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.avoqado.pos.core.delegates.SnackbarDelegate
import com.avoqado.pos.core.navigation.NavigationDispatcher
import com.avoqado.pos.core.navigation.NavigationManager
import com.avoqado.pos.core.navigation.NavigationManagerImpl
import com.avoqado.pos.router.AppRouter
import com.avoqado.pos.ui.theme.DemoandroidsdkmentaTheme
import com.menta.android.restclient.core.RestClientConfiguration.configure
import kotlinx.coroutines.launch

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

        setContent {
            AppRouter(
                navigationDispatcher = navigationDispatcher,
                snackbarDelegate = snackbarDelegate,
                context = this
            )
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
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DemoandroidsdkmentaTheme {
        Greeting("Android")
    }
}