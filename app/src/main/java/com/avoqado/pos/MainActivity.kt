package com.avoqado.pos

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.avoqado.pos.ui.theme.DemoandroidsdkmentaTheme
import com.avoqado.pos.views.InitActivity
import com.avoqado.pos.views.InputAmountActivity
import com.avoqado.pos.views.MenuActivity
import com.menta.android.restclient.core.Storage

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val storage = Storage(this)
        if (storage.getIdToken().isNotEmpty()) {
            Intent(this, InputAmountActivity::class.java)
                .let(::startActivity)
        } else {
            Intent(this, InitActivity::class.java)
                .let(::startActivity)
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