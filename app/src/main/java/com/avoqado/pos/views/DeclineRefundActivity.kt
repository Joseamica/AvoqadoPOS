package com.avoqado.pos.views

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.avoqado.pos.R
import com.avoqado.pos.ui.screen.ErrorScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DeclineRefundActivity : ComponentActivity() {

    private val message: String by lazy {
        intent.getStringExtra("message").toString()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ErrorScreen(title = "Ocurrió un error", message = message)
        }
        goToGetTransactions()
    }

    private fun goToGetTransactions() {
        Handler(Looper.getMainLooper()).postDelayed(
            {
                CoroutineScope(Dispatchers.Main).launch {
                    Log.i(TAG, "goToGetTransactions")
                    Intent(this@DeclineRefundActivity, MenuActivity::class.java)
                        .let(::startActivity)
                }
            }, 3000
        )
    }


    companion object {
        const val TAG = "DeclineRefundActivity"
    }
}