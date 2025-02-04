package com.avoqado.pos.views

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.res.stringResource
import com.avoqado.pos.AppfinRestClientConfigure
import com.avoqado.pos.OperationFlowHolder
import com.avoqado.pos.R
import com.avoqado.pos.customerId
import com.avoqado.pos.merchantId
import com.avoqado.pos.ui.screen.ProcessingOperationScreen
import com.avoqado.pos.views.SuccessPaymentActivity.Companion
import com.menta.android.common_cross.util.CURRENCY_LABEL_ARG
import com.menta.android.common_cross.util.CURRENCY_LABEL_MX
import com.menta.android.core.model.Currency
import com.menta.android.core.model.OperationFlow
import com.menta.android.core.viewmodel.bin.BinValidationData
import com.menta.android.emv.i9100.reader.util.CardType
import com.menta.android.restclient.core.RestClientConfiguration

class CardRulesValidationActivity : ComponentActivity() {

    private lateinit var binValidationData: BinValidationData
    private val bin: String by lazy {
        intent.getStringExtra("bin").toString()
    }
    private val currency: String by lazy {
        intent.getStringExtra("currency").toString()
    }
    private val operationFlow: OperationFlow?
        get() = OperationFlowHolder.operationFlow

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("${TAG}-AvoqadoTest", "New instance of ${TAG}")
        enableEdgeToEdge()
        setContent {
            ProcessingOperationScreen(
                title = stringResource(id = R.string.wait_payment),
                message = stringResource(id = R.string.whileServiceBinPaymentProcess)
            )

        }
        RestClientConfiguration.configure(AppfinRestClientConfigure())
        binValidationData = BinValidationData(this)
        binValidationData.setOperationFlow(
            operationFlow = operationFlow!!,
            merchantId = merchantId,
            customerId = customerId,
            currency = if(Currency.MX.name == currency) CURRENCY_LABEL_MX else CURRENCY_LABEL_ARG,
            interest = false
        )
        Log.i("", "bin: $bin")
        binValidationData.doBinValidation(bin)
        binValidationData.binValidationResponse.observe(this) {
            if (it.status == "FOUND") {
                binValidationData.setCardBrand(it.brand)
                val cardType: String = when (it.type) {
                    "C" -> {
                        CardType.CREDIT.name
                    }

                    "D" -> {
                        CardType.DEBIT.name
                    }

                    else -> {
                        CardType.PREPAID.name
                    }
                }
                binValidationData.setCardType(cardType)
                binValidationData.setIsInternational(it.isInternational ?: false)

                operationFlow!!.installments = "01"
                Log.i(TAG, "Ir directo al pago")
                val intent = Intent(this, DoPaymentActivity::class.java)
                startActivity(intent)
                finish()

            } else {
                Log.i(TAG, "Bin no encontrado")
                val brandsAvailable = it.brandsAvailable
                Log.i(TAG, "Marcas y tipos disponibles: $brandsAvailable")

                //TODO el cliente puede construir pantallas para selección manual, tomando como input brandsAvailable. Además, debe construir el consumo de installments, ejemplo:
                getInstallments(binValidationData)
            }
        }
    }

    override fun onBackPressed() {
    }

    private fun getInstallments(binValidationViewModel: BinValidationData) {
        val brand = "VISA"
        val cardType = "CREDIT"
        val isInternational = false
        binValidationViewModel.getInstallments(
            brand,
            paymentMethod = cardType,
            isInternational = isInternational
        )
        binValidationViewModel.getInstallmentsResponse.observe(this) {
            Log.i(TAG, "installments: ${it.installments}")

        }
    }


    companion object {
        const val TAG = "BinValidationActivity"
    }
}