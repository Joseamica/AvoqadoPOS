package com.avoqadoapp.screens

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.avoqadoapp.router.MainActivity
import com.avoqadoapp.screens.ProcessingOperationScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class DeclinedPaymentActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProcessingOperationScreen(title = "Ocurrió un error", message = "El pago no fue aprobado")
        }
        goToInputAmount()
    }

    private fun goToInputAmount() {
        Handler(Looper.getMainLooper()).postDelayed(
            {
                CoroutineScope(Dispatchers.Main).launch {
                    Timber.i( "goToInputAmount")
                    Intent(this@DeclinedPaymentActivity, MainActivity::class.java)
                        .let(::startActivity)
                }
            }, 3000
        )
    }


    companion object {
        const val TAG = "SuccessMessageActivity"
    }
}