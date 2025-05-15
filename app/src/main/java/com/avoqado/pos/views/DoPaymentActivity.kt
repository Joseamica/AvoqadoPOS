package com.avoqado.pos.views

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.res.stringResource
import com.avoqado.pos.AppfinRestClientConfigure
import com.avoqado.pos.AvoqadoApp
import com.avoqado.pos.MainActivity
import com.avoqado.pos.OperationFlowHolder
import com.avoqado.pos.R
import com.avoqado.pos.core.domain.models.AvoqadoError
import com.avoqado.pos.core.domain.models.SplitType
import com.avoqado.pos.core.presentation.model.enums.Acquirer
import com.avoqado.pos.core.presentation.utils.Utils.incrementBatch
import com.avoqado.pos.core.presentation.utils.toAmountMXDouble
import com.avoqado.pos.customerId
import com.avoqado.pos.features.payment.presentation.navigation.PaymentDests
import com.avoqado.pos.merchantId
import com.avoqado.pos.terminalId
import com.avoqado.pos.ui.screen.ProcessingOperationScreen
import com.google.gson.Gson
import com.menta.android.common_cross.util.CURRENCY_LABEL_ARG
import com.menta.android.common_cross.util.CURRENCY_LABEL_MX
import com.menta.android.core.model.Adquirer
import com.menta.android.core.model.LocalData
import com.menta.android.core.model.OperationFlow
import com.menta.android.core.model.OperationResponseCode
import com.menta.android.core.utils.DateUtil
import com.menta.android.core.viewmodel.DoProcessAdquirerOperationData
import com.menta.android.emv.i9100.reader.emv.EMVImpl
import com.menta.android.keys.admin.core.repository.DeviceKeyStorage
import com.menta.android.keys.admin.core.repository.parametro.DBDefines
import com.menta.android.keys.admin.core.repository.parametro.ParametroDB
import com.menta.android.restclient.core.RestClientConfiguration
import timber.log.Timber
import java.time.LocalDateTime

class DoPaymentActivity : ComponentActivity() {
    private val operationFlow: OperationFlow?
        get() = OperationFlowHolder.operationFlow

    private val splitType: SplitType by lazy {
        SplitType.valueOf(intent.getStringExtra("splitType").toString())
    }
    private val waiterName: String by lazy {
        intent.getStringExtra("waiterName").toString()
    }

    private val currentUser = AvoqadoApp.sessionManager.getAvoqadoSession()
    private val operationPreference = AvoqadoApp.sessionManager.getOperationPreference()
    private val venueInfo = AvoqadoApp.sessionManager.getVenueInfo()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProcessingOperationScreen(
                title = stringResource(id = R.string.wait_payment),
                message = stringResource(id = R.string.whileServicePaymentProcess),
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
                acquirerId = if (operationFlow?.amount?.currency == CURRENCY_LABEL_MX) Acquirer.BANORTE.name else Acquirer.GPS.name,
                merchantId =
                    venueInfo?.menta?.let {
                        if (operationPreference) {
                            it.merchantIdA
                        } else {
                            it.merchantIdB
                        }
                    } ?: "",
                aesKey = deviceKeyStorage.getAesKeyExecute(),
                ivKey = deviceKeyStorage.getIvKeyExecute(),
                banorteKey = deviceKeyStorage.getKeyExecute(),
                customerId = customerId,
                currencyLabel = if (operationFlow?.amount?.currency == CURRENCY_LABEL_MX) CURRENCY_LABEL_MX else CURRENCY_LABEL_ARG,
            )
        operationFlow?.additional_info = "TEST"
        val emvImpl = EMVImpl()
        val storage = AvoqadoApp.storage
        val doPayment =
            DoProcessAdquirerOperationData(
                context = this,
                localData = localData,
                version = "",
                device = emvImpl,
                storage = storage,
                transactionDate = DateUtil.getLocalDateTimeWithOffset(),
                dataFlow = operationFlow!!,
            )

        doPayment.doOperation(operationType = operationFlow?.transactionType!!)
        doPayment.operationResponse.observe(this) {
            it.data?.let { response ->
                val operationResponse = response as Adquirer
                val gson = Gson()
                val operationResponseJson = gson.toJson(operationResponse)

                Log.i(TAG, "operationResponseJson: $operationResponseJson")
                Log.i(TAG, "response.code: ${operationResponse.response?.code}")
                Log.i(TAG, "response.description: ${operationResponse.response?.description}")
                Log.i(TAG, "response.name: ${operationResponse.response?.name}")
                Timber.i("Operation response: $operationResponseJson")

                if (operationResponse.status?.code == OperationResponseCode.APPROVED) {
                    Log.i(TAG, "PaymentId: ${operationResponse.id}")
                    Log.i(TAG, "OperationNumber: ${operationResponse.ticketId}")

                    AvoqadoApp.paymentRepository.getCachePaymentInfo()?.let { info ->
                        AvoqadoApp.paymentRepository.setCachePaymentInfo(
                            paymentInfoResult =
                                info.copy(
                                    paymentId = operationResponse.ticketId.toString(),
                                    tipAmount =
                                        operationResponse.amount.breakdown
                                            .firstOrNull { breakdown ->
                                                breakdown.description == "TIP"
                                            }?.amount
                                            ?.toAmountMXDouble()
                                            ?: 0.0,
                                    subtotal =
                                        operationResponse.amount.breakdown
                                            .firstOrNull { breakdown ->
                                                breakdown.description == "OPERATION"
                                            }?.amount
                                            ?.toAmountMXDouble()
                                            ?: 0.0,
                                    date = LocalDateTime.now(),
                                    rootData = operationResponseJson,
                                    splitType = splitType,
                                    waiterName = waiterName,
                                ),
                        )
                    }

                    val intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    intent.putExtra("navigate_to", PaymentDests.PaymentResult.route)
                    startActivity(intent)
                } else {
                    Timber.e(AvoqadoError.BasicError(message = "Pago declinado"), "OperationFlow:", operationFlow)
                    val intent = Intent(this, DeclinedPaymentActivity::class.java)
                    startActivity(intent)
                }
            } ?: run {
                val intent = Intent(this, DeclinedPaymentActivity::class.java)
                startActivity(intent)
            }

            finish()
        }
    }

    override fun onBackPressed() {
    }

    companion object {
        const val TAG = "DoPaymentActivity"
    }
}
