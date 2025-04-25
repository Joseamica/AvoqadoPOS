package com.avoqado.pos.views

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.avoqado.pos.AppfinRestClientConfigure
import com.avoqado.pos.CURRENCY_LABEL
import com.avoqado.pos.ui.screen.ProcessingOperationScreen
import com.menta.android.core.model.SendEmailRequest
import com.menta.android.core.viewmodel.SendEmailData
import com.menta.android.keys.admin.core.remote.keys.Resource
import com.menta.android.restclient.core.RestClientConfiguration

class SendTicketActivity : ComponentActivity() {
    private val email: String by lazy {
        intent.getStringExtra("email").toString()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProcessingOperationScreen("Aguarde", "Estamos enviando el comprobante.")
        }
        RestClientConfiguration.configure(AppfinRestClientConfigure())
        val emailData = SendEmailData(context = applicationContext)
        val map =
            hashMapOf(
                "date_terminal" to "27/06/23",
                "time_terminal" to "12:44:08",
                "operation_type" to "PAYMENT",
                "merchant_name" to "[Nombre de comercio]",
                "merchant_address" to "Dirección del comercio",
                "operation_number" to "181625710",
                "masked_number" to "***0190",
                "reader_type" to "CONTACTLESS",
                "amount" to "100,00",
                "installments" to "01",
                "card_brand" to "MASTERCARD",
                "currency" to CURRENCY_LABEL,
                "powered_by_menta_footer" to "Leyenda: Powered by menta",
                "card_type" to "Crédito",
                "card_aid" to "A0000000041010C123456789",
                "subject" to "Comprobante de compra en [Nombre de comercio]",
                "subtitle" to "Te acercamos el comprobante de tu compra en [Nombre de comercio]",
            )

        // TODO: agegar nueva informacion para envio de email
        val sendEmailRequest =
            SendEmailRequest(
                email,
                "PAYMENT",
                customer_id = "",
                merchant_id = "",
                operation_id = "",
                context = "",
                content = map,
            )
        emailData.sendEmail(sendEmailRequest = sendEmailRequest)
        emailData.sendEmailResponse.observe(this) { result ->
            Log.i(TAG, "Resultado: $result")
            when (result) {
                is Resource.Success -> {
                    Log.i(TAG, "Email enviado correctamente")
                    val intent = Intent(this, SuccessMessageActivity::class.java)
                    intent.putExtra("message", "Email enviado correctamente")
                    startActivity(intent)
                }

                else -> {
                    Log.i(TAG, "Error al enviar mail")
                    val intent = Intent(this, ErrorActivity::class.java)
                    intent.putExtra("message", "Revise los datos enviados")
                    startActivity(intent)
                }
            }
        }
    }

    companion object {
        const val TAG = "SendTicketActivity"
    }
}
