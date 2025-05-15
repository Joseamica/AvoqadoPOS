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
import com.avoqado.pos.OperationFlowHolder
import com.avoqado.pos.R
import com.avoqado.pos.core.domain.models.SplitType
import com.avoqado.pos.customerId
import com.avoqado.pos.ui.screen.ProcessingOperationScreen
import com.menta.android.common_cross.util.CURRENCY_LABEL_ARG
import com.menta.android.common_cross.util.CURRENCY_LABEL_MX
import com.menta.android.common_cross.util.CardType
import com.menta.android.core.model.Currency
import com.menta.android.core.model.OperationFlow
import com.menta.android.core.viewmodel.bin.BinValidationData
import com.menta.android.restclient.core.RestClientConfiguration
import timber.log.Timber

class CardRulesValidationActivity : ComponentActivity() {
    private lateinit var binValidationData: BinValidationData
    private val bin: String by lazy {
        intent.getStringExtra("bin").toString()
    }
    private val currency: String by lazy {
        intent.getStringExtra("currency").toString()
    }

    private val splitType: SplitType by lazy {
        SplitType.valueOf(intent.getStringExtra("splitType").toString())
    }
    private val waiterName: String by lazy {
        intent.getStringExtra("waiterName").toString()
    }

    private val currentUser = AvoqadoApp.sessionManager.getAvoqadoSession()
    private val venueInfo = AvoqadoApp.sessionManager.getVenueInfo()
    private val operationPreference = AvoqadoApp.sessionManager.getOperationPreference()

    private val operationFlow: OperationFlow?
        get() = OperationFlowHolder.operationFlow

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.i("New instance of CardRulesValidationActivity")
        enableEdgeToEdge()
        setContent {
            ProcessingOperationScreen(
                title = stringResource(id = R.string.wait_payment),
                message = stringResource(id = R.string.whileServiceBinPaymentProcess),
            )
        }
        RestClientConfiguration.configure(AppfinRestClientConfigure())
        binValidationData = BinValidationData(this)
        binValidationData.setOperationFlow(
            operationFlow = operationFlow!!,
            merchantId =
                venueInfo?.menta?.let {
                    if (operationPreference) {
                        it.merchantIdA
                    } else {
                        it.merchantIdB
                    }
                } ?: "",
            customerId = customerId,
            currency = if (Currency.MX.name == currency) CURRENCY_LABEL_MX else CURRENCY_LABEL_ARG,
            interest = false,
        )
        Timber.i("bin: $bin")
        binValidationData.doBinValidation(bin)
        binValidationData.binValidationResponse.observe(this) {
            if (it.status == "FOUND") {
                binValidationData.setCardBrand(it.brand)
                val cardType: String =
                    when (it.type) {
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
                Timber.i("Ir directo al pago")
                val intent =
                    Intent(this, DoPaymentActivity::class.java).apply {
                        putExtra("splitType", splitType.value)
                        putExtra("waiterName", waiterName)
                    }
                startActivity(intent)
                finish()
            } else {
                Timber.i("Bin no encontrado")
                val brandsAvailable = it.brandsAvailable
                Timber.i("Marcas y tipos disponibles: $brandsAvailable")

                // TODO el cliente puede construir pantallas para selección manual, tomando como input brandsAvailable. Además, debe construir el consumo de installments, ejemplo:
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
            isInternational = isInternational,
        )
        binValidationViewModel.getInstallmentsResponse.observe(this) {
            Timber.i("installments: ${it.installments}")
        }
    }
}
