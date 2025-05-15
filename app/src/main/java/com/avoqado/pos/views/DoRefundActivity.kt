package com.avoqado.pos.views

import android.content.Intent
import android.os.Bundle
import timber.log.Timber
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.res.stringResource
import com.avoqado.pos.ACQUIRER_NAME
import com.avoqado.pos.AppfinRestClientConfigure
import com.avoqado.pos.CURRENCY_LABEL
import com.avoqado.pos.OperationFlowHolder
import com.avoqado.pos.R
import com.avoqado.pos.core.presentation.utils.Utils.incrementBatch
import com.avoqado.pos.customerId
import com.avoqado.pos.merchantId
import com.avoqado.pos.terminalId
import com.avoqado.pos.ui.screen.ProcessingOperationScreen
import com.google.gson.Gson
import com.menta.android.core.model.Adquirer
import com.menta.android.core.model.LocalData
import com.menta.android.core.model.OperationFlow
import com.menta.android.core.model.OperationResponseCode
import com.menta.android.core.model.OperationType
import com.menta.android.core.utils.DateUtil
import com.menta.android.core.viewmodel.DoProcessAdquirerOperationData
import com.menta.android.emv.i9100.reader.emv.EMVImpl
import com.menta.android.keys.admin.core.repository.DeviceKeyStorage
import com.menta.android.keys.admin.core.repository.parametro.DBDefines
import com.menta.android.keys.admin.core.repository.parametro.ParametroDB
import com.menta.android.restclient.core.RestClientConfiguration
import com.menta.android.restclient.core.Storage

class DoRefundActivity : ComponentActivity() {
    private val operationFlow: OperationFlow?
        get() = OperationFlowHolder.operationFlow

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_do_refund)
        setContent {
            ProcessingOperationScreen(
                title = stringResource(id = R.string.wait_payment),
                message = stringResource(id = R.string.whileServiceRefundProcess),
            )
        }
        RestClientConfiguration.configure(AppfinRestClientConfigure())
        val deviceKeyStorage = DeviceKeyStorage(context = this)
        val dbParams = ParametroDB(this)
        incrementBatch(this)
        val localData =
            LocalData(
                batch = dbParams.getValueParam(DBDefines.batch).toLong(),
                ticket = dbParams.getValueParam(DBDefines.ticket).toLong(),
                trace = dbParams.getValueParam(DBDefines.trace).toLong(),
                terminalId = terminalId,
                acquirerId = ACQUIRER_NAME,
                merchantId = merchantId,
                aesKey = deviceKeyStorage.getAesKeyExecute(),
                ivKey = deviceKeyStorage.getIvKeyExecute(),
                banorteKey = deviceKeyStorage.getKeyExecute(),
                customerId = customerId,
                currencyLabel = CURRENCY_LABEL,
            )
        val emvImpl = EMVImpl()
        val storage = Storage(context = this)
        val doRefund =
            DoProcessAdquirerOperationData(
                context = this,
                localData = localData,
                version = "",
                device = emvImpl,
                storage = storage,
                transactionDate = DateUtil.getLocalDateTimeWithOffset(),
                dataFlow = operationFlow!!,
            )

        doRefund.doOperation(operationType = OperationType.REFUND)
        doRefund.operationResponse.observe(this) {
            Timber.i("Resultado de la devolución: $it")
            Timber.i("Error message: ${it.status.message}")
            it.data?.let { response ->
                val operationResponse = response as Adquirer
                val gson = Gson()
                val operationResponseJson = gson.toJson(operationResponse)

                Timber.i("operationResponseJson: $operationResponseJson")
                Timber.i("response.code: ${operationResponse.response?.code}")
                Timber.i("response.description: ${operationResponse.response?.description}")
                Timber.i("response.name: ${operationResponse.response?.name}")

                if (operationResponse.status?.code == OperationResponseCode.APPROVED) {
                    Timber.i("Id: ${operationResponse.id}")
                    Timber.i("OperationNumber: ${operationResponse.ticketId}")
                    val intent = Intent(this, SuccessRefundActivity::class.java)
                    startActivity(intent)
                } else {
                    Timber.i("Devolución declinada!")
                    val intent = Intent(this, DeclineRefundActivity::class.java)
                    intent.putExtra("message", it.status.message)
                    startActivity(intent)
                }
            } ?: run {
                Timber.i("Devolución no procesada!")
                val intent = Intent(this, DeclineRefundActivity::class.java)
                intent.putExtra("message", it.status.message)
                startActivity(intent)
            }
        }
    }
}
