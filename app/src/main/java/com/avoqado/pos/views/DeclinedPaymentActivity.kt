package com.avoqado.pos.views

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import timber.log.Timber
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.avoqado.pos.MainActivity
import com.avoqado.pos.ui.screen.ErrorScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DeclinedPaymentActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ErrorScreen(title = "Ocurrió un error", message = "El pago no fue aprobado")
        }
        goToInputAmount()
    }

    override fun onBackPressed() {
    }

    private fun goToInputAmount() {
        Handler(Looper.getMainLooper()).postDelayed(
            {
                CoroutineScope(Dispatchers.Main).launch {
                    Timber.i("goToInputAmount")
                    Intent(this@DeclinedPaymentActivity, MainActivity::class.java)
                        .let(::startActivity)
                    finish()
                }
            },
            3000,
        )
    }
}
