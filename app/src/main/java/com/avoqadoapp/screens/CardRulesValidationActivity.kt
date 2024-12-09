package com.avoqadoapp.screens

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.avoqadoapp.core.OperationFlowHolder
import com.avoqadoapp.customerId
import com.avoqadoapp.data.AppRestClientConfigure
import com.avoqadoapp.merchantId
import com.avoqadoapp.ui.theme.textColor
import com.avoqadoapp.ui.theme.textlightGrayColor
import com.menta.android.common_cross.util.CURRENCY_LABEL_ARG
import com.menta.android.common_cross.util.CURRENCY_LABEL_MX
import com.menta.android.core.model.Currency
import com.menta.android.core.model.OperationFlow
import com.menta.android.core.viewmodel.bin.BinValidationData
import com.menta.android.emv.i9100.reader.util.CardType
import com.menta.android.restclient.core.RestClientConfiguration
import timber.log.Timber

class CardRulesValidationActivity: ComponentActivity() {
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
        enableEdgeToEdge()
        setContent {
            ProcessingOperationScreen(
                title = "Aguarde",
                message = "Mientras procesamos su pago"
            )

        }
        RestClientConfiguration.configure(AppRestClientConfigure())
        binValidationData = BinValidationData(this)
        binValidationData.setOperationFlow(
            operationFlow = operationFlow!!,
            merchantId = merchantId,
            customerId = customerId,
            currency = if(Currency.MX.name == currency) CURRENCY_LABEL_MX else CURRENCY_LABEL_ARG,
            interest = false
        )
        Timber.d( "bin: $bin")
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

                when (cardType) {
//                    CardType.CREDIT.name -> {
//                        Timber.d( "Mostrar pantalla de cuotas disponibles")
//                        val intent = Intent(this, InstallmentsActivity::class.java).apply {
//                            putParcelableArrayListExtra(
//                                "installment_list",
//                                ArrayList(it.installments.installments)
//                            )
//                        }
//                        startActivity(intent)
//                    }

                    else -> { //Debit y Prepaid
                        operationFlow!!.installments = "01"
                        Timber.i( "Ir directo al pago")
                        val intent = Intent(this, DoPaymentActivity::class.java)
                        startActivity(intent)
                    }
                }

            } else {
                Timber.i( "Bin no encontrado")
                val brandsAvailable = it.brandsAvailable
                Timber.i( "Marcas y tipos disponibles: $brandsAvailable")

                //TODO el cliente puede construir pantallas para selección manual, tomando como input brandsAvailable. Además, debe construir el consumo de installments, ejemplo:
            }
        }
    }
}

@Composable
fun ProcessingOperationScreen(
    title: String,
    message: String,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(textlightGrayColor)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(120.dp), horizontalAlignment = Alignment.CenterHorizontally
        ) {
           CircularProgressIndicator()
        }
        Column(
            modifier = Modifier
                .padding(start = 24.dp,end=24.dp, bottom = 50.dp)
        ) {
            Text(
                text = title,
                fontSize = 35.sp,
                color = textColor,
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = message,
                fontSize = 25.sp,
                color = textColor,
            )
        }
    }
}