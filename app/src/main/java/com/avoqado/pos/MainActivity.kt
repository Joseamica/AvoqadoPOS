package com.avoqado.pos
import android.util.Log
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.avoqado.pos.core.delegates.SnackbarDelegate
import com.avoqado.pos.core.navigation.NavigationDispatcher
import com.avoqado.pos.core.navigation.NavigationManager
import com.avoqado.pos.core.navigation.NavigationManagerImpl
import com.avoqado.pos.router.AppRouter
import com.avoqado.pos.ui.theme.DemoandroidsdkmentaTheme
import com.avoqado.pos.views.InitActivity
import com.avoqado.pos.views.MenuActivity
import com.menta.android.restclient.core.Storage

class MainActivity : ComponentActivity() {


    lateinit var navigationManager: NavigationManager
    lateinit var navigationDispatcher: NavigationDispatcher
    val snackbarDelegate: SnackbarDelegate = SnackbarDelegate()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        navigationManager = NavigationManagerImpl()
        navigationDispatcher = NavigationDispatcher(navigationManager)

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