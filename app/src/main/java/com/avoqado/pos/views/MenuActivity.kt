package com.avoqado.pos.views

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.RemoteException
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Observer
import com.avoqado.pos.CURRENCY_LABEL
import com.avoqado.pos.R
import com.avoqado.pos.ui.screen.PrimaryButton
import com.avoqado.pos.core.presentation.theme.primary
import com.menta.android.core.model.OperationType
import com.menta.android.printer.i9100.core.DevicePrintImpl
import com.menta.android.printer.i9100.model.Align
import com.menta.android.printer.i9100.model.TextFormat
import com.menta.android.printer.i9100.util.INSTALLMENT_LABEL
import com.menta.android.printer.i9100.util.TOTAL_LABEL
import java.util.Locale

class MenuActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MenuScreen()
        }
    }

    override fun onBackPressed() {
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    fun MenuScreen() {

        MaterialTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colors.background
            ) {
                Column {
                    TopAppBar(
                        title = { Text(text = "Avoqado POS") },
                        backgroundColor = primary
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        PrimaryButton(text = "Hacer un pago",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp)
                                .height(57.dp)
                                .align(Alignment.End),
                            onClick = {
                                Log.i("", "Hacer un pago")
                                val intent = Intent(this@MenuActivity, InputAmountActivity::class.java)
                                intent.putExtra("operationType", OperationType.PAYMENT.name)
                                intent.putExtra("currency", CURRENCY_LABEL)
                                startActivity(intent)
                            })
                        PrimaryButton(text = "Hacer una devolución",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp)
                                .height(57.dp)
                                .align(Alignment.End),
                            onClick = {
                                Log.i("", "Hacer una devolución")
                                Intent(this@MenuActivity, GetTransactionsActivity::class.java)
                                    .let(::startActivity)
                            })
                        PrimaryButton(text = "Probar impresora",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp)
                                .height(57.dp)
                                .align(Alignment.End),
                            onClick = {
                                Log.i("", "Probar impresora")
                                val devicePrintImpl = DevicePrintImpl(context = applicationContext)
                                val status = devicePrintImpl.getStatus()
                                Log.i(TAG, "status impresora: $status")
                                if (status == 0) {
                                    val thread = Thread {
                                        devicePrintImpl.addLine(
                                            TextFormat(align = Align.CENTER, bold = true, font = 1),
                                            "Nombre del comercio"
                                        )
                                        devicePrintImpl.addLine(
                                            TextFormat(align = Align.CENTER, bold = false),
                                            "Dirección del comercio"
                                        )
                                        devicePrintImpl.addLine(
                                            TextFormat(align = Align.CENTER, bold = false),
                                            "DNI del comercio"
                                        )
                                        devicePrintImpl.addLine(
                                            TextFormat(align = Align.CENTER, bold = false),
                                            "CUIT del comercio"
                                        )

                                        devicePrintImpl.addLinebreak(1)
                                        devicePrintImpl.addLine(
                                            TextFormat(align = Align.CENTER, bold = true),
                                            "Pago con Tarjeta de Crédito"
                                        )

                                        devicePrintImpl.addLinebreak(1)
                                        devicePrintImpl.addDoubleColumnText(
                                            TextFormat(),
                                            "Número de Operación",
                                            "#123456"
                                        )
                                        devicePrintImpl.addDoubleColumnText(
                                            TextFormat(),
                                            "Tarj: ***1234",
                                            "Visa"
                                        )
                                        devicePrintImpl.addDoubleColumnText(
                                            TextFormat(),
                                            "CONTACTLESS",
                                            ""
                                        )
                                        devicePrintImpl.addLinebreak(1)
                                        try {
                                            devicePrintImpl.addImage(
                                                getBitmap(
                                                    R.drawable.line,
                                                    applicationContext
                                                )
                                            )
                                        } catch (e: RemoteException) {
                                            e.printStackTrace()
                                        }

                                        devicePrintImpl.addDoubleColumnText(
                                            TextFormat(bold = true, font = 1),
                                            TOTAL_LABEL.uppercase(Locale.getDefault()),
                                            "10,00"
                                        )
                                        devicePrintImpl.addDoubleColumnText(
                                            TextFormat(), INSTALLMENT_LABEL, "01"
                                        )

                                        devicePrintImpl.addDoubleColumnText(
                                            TextFormat(),
                                            CURRENCY_LABEL,
                                            CURRENCY_LABEL
                                        )
                                        devicePrintImpl.addLinebreak(1)

                                        try {
                                            devicePrintImpl.startPrint()
                                            Handler(Looper.getMainLooper()).post {
                                                devicePrintImpl.result.observeForever(resultObserver)
                                            }
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                    }
                                    thread.start()
                                }

                            })

                        PrimaryButton(text = "Enviar ticket por mail",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp)
                                .height(57.dp)
                                .align(Alignment.End),
                            onClick = {
                                Log.i("", "Enviar ticket por mailn")
                                Intent(this@MenuActivity, InputMailActivity::class.java)
                                    .let(::startActivity)
                            })

                        PrimaryButton(text = "Hacer una preautorizacion",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp)
                                .height(57.dp)
                                .align(Alignment.End),
                            onClick = {
                                Log.i("", "Hacer una preautorizacion")
                                val intent = Intent(this@MenuActivity, InputAmountActivity::class.java)
                                intent.putExtra("operationType", OperationType.PREAUTHORIZATION.name)
                                intent.putExtra("currency", CURRENCY_LABEL)
                                startActivity(intent)
                            })
                    }
                }
            }
        }

    }

    private val resultObserver = Observer<Int> { result ->
        if (result == 0) {
            Log.i(TAG, "Impresión exitosa")
        } else {
            Log.i(TAG, "Error de impresión: $result")
        }
    }

    private fun getBitmap(drawableRes: Int, context: Context): Bitmap {
        val drawable = AppCompatResources.getDrawable(context, drawableRes)
        val canvas = Canvas()
        val bitmap = Bitmap.createBitmap(
            drawable!!.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        canvas.setBitmap(bitmap)
        drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        drawable.draw(canvas)
        return bitmap
    }

    companion object {
        const val TAG = "MenuActivity"
    }
}