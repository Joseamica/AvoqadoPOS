package com.avoqadoapp.screens.cardProcess

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.avoqadoapp.customerId
import com.avoqadoapp.merchantId
import com.menta.android.common_cross.util.CURRENCY_LABEL_ARG
import com.menta.android.common_cross.util.CURRENCY_LABEL_MX
import com.menta.android.common_cross.util.StatusResult
import com.menta.android.core.model.Currency
import com.menta.android.core.viewmodel.CardProcessData
import com.menta.android.core.viewmodel.bin.BinValidationData
import com.menta.android.emv.i9100.reader.util.InputMode
import timber.log.Timber

@Composable
fun CardProcessScreen(
    viewModel: CardProcessViewModel
){
    val cardProcessData = remember {
        CardProcessData()
    }

    val context = LocalContext.current
    val selectApp by cardProcessData.selectApp.observeAsState()
    val navigate by cardProcessData.navigate.observeAsState()

    val binValidationData: BinValidationData = BinValidationData(context)

    LaunchedEffect(key1 = Unit) {
        cardProcessData.findCardProcess(
            operationFlow = viewModel.doOperationFlow(),
            cardData = viewModel.getCardData(),
            context = context,
            inputModeType = InputMode.ALL
        )
    }

    LaunchedEffect(key1 = selectApp) {
        selectApp?.let {
            val builder = AlertDialog.Builder(context)
            builder.setItems(it.emv) { _: DialogInterface?, which: Int ->
                try {
                    cardProcessData.setIndexApp(which)
                    it.futureTask.run()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            builder.create().show()
        }
    }

    LaunchedEffect(key1 = navigate) {
        navigate?.let {
            when (it) {
                is Bundle -> {
                    Timber.d("Bundle: $it")
                    val status = it.get("status")
                    val statusResult: StatusResult = status as StatusResult
                    Timber.d("statusResult: $statusResult")
//                    val intent = Intent(this, CardErrorActivity::class.java)
//                    intent.putExtra("status", statusResult)
//                    startActivity(intent)
                }

                is String -> {
                    val operationType = viewModel.operationType
                    if (operationType == "PAYMENT" || operationType == "PREAUTHORIZATION") {
                        Timber.d("BIN de la tarjeta: $it")
                        //Validar BIN
                        binValidationData.setOperationFlow(
                            operationFlow = viewModel.operationFlow,
                            merchantId = merchantId,
                            customerId = customerId,
                            currency = if(Currency.MX.name == viewModel.currency) CURRENCY_LABEL_MX else CURRENCY_LABEL_ARG,
                            interest = false
                        )
                        binValidationData.doBinValidation(it)
                    }
                }
            }
        }
    }
}