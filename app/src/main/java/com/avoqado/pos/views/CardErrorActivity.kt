package com.avoqado.pos.views

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.avoqado.pos.CURRENCY_LABEL
import com.avoqado.pos.MainActivity
import com.avoqado.pos.R
import com.avoqado.pos.ui.screen.PrimaryButton
import com.avoqado.pos.ui.theme.textColor
import com.menta.android.common_cross.util.StatusResult
import com.menta.android.core.model.OperationType
import com.menta.android.core.utils.StringUtils


class CardErrorActivity : ComponentActivity() {

    private val amount: String by lazy {
        intent.getStringExtra("amount").toString()
    }
    private val tipAmount: String by lazy {
        intent.getStringExtra("tipAmount").toString()
    }
    private val currency: String by lazy {
        intent.getStringExtra("currency").toString()
    }
    private val operationType: String by lazy {
        intent.getStringExtra("operationType").toString()
    }

    private var statusResult: StatusResult? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var messageToShow = "Error en el proceso de lectura"
            val bundle = intent.extras
            bundle?.let {
                statusResult = it.getParcelable("status")
                statusResult?.apply {
                    messageToShow = message
                    Log.i(TAG, "Title: $title")
                    Log.i(TAG, "Message: $message")
                    Log.i(TAG, "StatusType: $statusType")
                    Log.i(TAG, "ShowButton: $showButton")
                    Log.i(TAG, "BtnTitle: $btnTitle")
                }
            }
            ErrorScreen(title = "Error de lectura", message = messageToShow)

        }
    }

    @Composable
    fun ErrorScreen(
        title: String,
        message: String
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentAlignment = Alignment.Center,
            content = {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_info),
                        contentDescription = null,
                    )
                }
            }
        )

        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(14.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                androidx.compose.material.Text(
                    modifier = Modifier
                        .width(309.dp)
                        .align(Alignment.Start),
                    text = title,
                    color = textColor,
                    fontSize = 35.sp,
                    fontWeight = FontWeight.Bold,
                )

                androidx.compose.material.Text(
                    modifier = Modifier
                        .width(309.dp)
                        .align(Alignment.Start),
                    text = message,
                    color = textColor,
                    fontSize = 25.sp
                )

                PrimaryButton(
                    text = "Reintentar",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(0.dp, 14.dp, 0.dp)
                        .height(57.dp)
                        .align(Alignment.End),
                    onClick = {
                        Log.i(TAG, "goToInputAmount")
                        Intent(this@CardErrorActivity, CardProcessActivity::class.java)
                            .apply {
                                putExtra("amount", amount)
                                putExtra("tipAmount", tipAmount)
                                putExtra("currency", currency)
                                putExtra("operationType", operationType)
                            }
                            .let(::startActivity)
                        finish()
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                PrimaryButton(
                    text = "Listo",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(0.dp, 14.dp, 0.dp)
                        .height(57.dp)
                        .align(Alignment.End),
                    onClick = {
                        Log.i(TAG, "goToInputAmount")
                        Intent(this@CardErrorActivity, MainActivity::class.java)
                            .let(::startActivity)
                        finish()
                    }
                )
            }
        }
    }

    companion object {
        const val TAG = "CardErrorActivity"
    }
}