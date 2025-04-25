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
import com.avoqado.pos.ui.screen.SuccessScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SuccessRefundActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SuccessScreen(message = "devolución realizada con éxito")
        }
        goToMenu()
    }

    private fun goToMenu() {
        Handler(Looper.getMainLooper()).postDelayed(
            {
                CoroutineScope(Dispatchers.Main).launch {
                    Log.i(TAG, "goToInputAmount")
                    Intent(this@SuccessRefundActivity, MainActivity::class.java)
                        .let(::startActivity)
                    finish()
                }
            },
            3000,
        )
    }

    companion object {
        const val TAG = "SuccessRefundActivity"
    }
}
