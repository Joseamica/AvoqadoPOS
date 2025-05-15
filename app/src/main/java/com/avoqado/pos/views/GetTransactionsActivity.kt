package com.avoqado.pos.views

import android.content.Intent
import android.os.Bundle
import timber.log.Timber
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.res.stringResource
import com.avoqado.pos.AppfinRestClientConfigure
import com.avoqado.pos.R
import com.avoqado.pos.customerId
import com.avoqado.pos.merchantId
import com.avoqado.pos.ui.screen.ProcessingOperationScreen
import com.menta.android.common_cross.util.StatusType
import com.menta.android.core.model.LastTrxRequest
import com.menta.android.core.viewmodel.TrxData
import com.menta.android.restclient.core.RestClientConfiguration

class GetTransactionsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProcessingOperationScreen(
                title = stringResource(id = R.string.wait_payment),
                message = stringResource(id = R.string.whileServiceTransactionProcess),
            )
        }
        RestClientConfiguration.configure(AppfinRestClientConfigure())
        val trxData = TrxData(this)
        val lastTrxRequest =
            LastTrxRequest(
                appVersion = "",
                operationType = "",
                merchantId = merchantId,
                customerId = customerId,
                userEmail = null,
                start = "2024-10-07T00:00:00Z",
                end = "2024-10-08T23:59:00Z",
                page = 0,
                size = 1000,
            )
        trxData.getLastTrx(lastTrxRequest = lastTrxRequest)
        trxData.getLastTrx.observe(this) { lisTrx ->
            lisTrx?.let {
                Timber.i("Transaccions response: $lisTrx")
                if (lisTrx.statusResult?.statusType == StatusType.SUCCESS) {
                    val intent = Intent(this, ListTransactionActivity::class.java)
                    intent.putExtra("transactionList", ArrayList(lisTrx.content))
                    startActivity(intent)
                } else {
                    Timber.i("Error en la consulta")
                }
            } ?: run {
                Timber.i("Transacciones no disponibles")
            }
        }
    }
}
