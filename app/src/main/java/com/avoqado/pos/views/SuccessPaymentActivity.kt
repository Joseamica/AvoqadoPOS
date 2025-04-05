package com.avoqado.pos.views

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.avoqado.pos.MainActivity
import com.avoqado.pos.OperationFlowHolder
import com.avoqado.pos.core.presentation.destinations.MainDests
import com.avoqado.pos.ui.screen.SuccessScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SuccessPaymentActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("$TAG-AvoqadoTest", "New instance of $TAG")
        enableEdgeToEdge()
        setContent {
            SuccessScreen(message = "pago realizado con éxito")
        }
        goToMenu()
    }

    override fun onBackPressed() {
    }

    private fun goToMenu() {
        Handler(Looper.getMainLooper()).postDelayed(
            {
                CoroutineScope(Dispatchers.Main).launch {
                    Log.i(TAG, "goToInputAmount")
                    //Clear Operation Flow
                    OperationFlowHolder.operationFlow = null
                    val intent = Intent(this@SuccessPaymentActivity, MainActivity::class.java)
                        .apply {
                            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                        }

                    startActivity(intent)
                    finish()
                }
            }, 3000
        )
    }


    companion object {
        const val TAG = "SuccessMessageActivity"
    }
}