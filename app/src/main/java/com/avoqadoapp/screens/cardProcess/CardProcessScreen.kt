package com.avoqadoapp.screens.cardProcess

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.avoqadoapp.core.utils.Utils.incrementBatch
import com.avoqadoapp.customerId
import com.avoqadoapp.merchantId
import com.avoqadoapp.terminalId
import com.google.gson.Gson
import com.menta.android.common_cross.util.Acquirer
import com.menta.android.common_cross.util.CURRENCY_LABEL_ARG
import com.menta.android.common_cross.util.CURRENCY_LABEL_MX
import com.menta.android.common_cross.util.StatusResult
import com.menta.android.core.datasource.definces.DBDefines
import com.menta.android.core.model.Adquirer
import com.menta.android.core.model.Currency
import com.menta.android.core.model.LocalData
import com.menta.android.core.model.OperationResponseCode
import com.menta.android.core.model.OperationResponseModel
import com.menta.android.core.utils.DateUtil
import com.menta.android.core.viewmodel.CardProcessData
import com.menta.android.core.viewmodel.DoProcessAdquirerOperationData
import com.menta.android.core.viewmodel.bin.BinValidationData
import com.menta.android.emv.i9100.reader.emv.EMVImpl
import com.menta.android.emv.i9100.reader.util.CardType
import com.menta.android.emv.i9100.reader.util.InputMode
import com.menta.android.keys.admin.core.repository.DeviceKeyStorage
import com.menta.android.keys.admin.core.repository.parametro.ParametroDB
import com.menta.android.restclient.core.Storage
import timber.log.Timber

@Composable
fun CardProcessScreen(
    viewModel: CardProcessViewModel,
    cardProcessData: CardProcessData,
    binValidationData: BinValidationData
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    var doPayment2: DoProcessAdquirerOperationData? = remember(key1 = state.isPaymentStarted) {
        null
    }

    val context = LocalContext.current
    val selectApp by cardProcessData.selectApp.observeAsState()
    val navigate by cardProcessData.navigate.observeAsState()


    val binResponse by binValidationData.binValidationResponse.observeAsState()
    val storage = Storage(context = context)
    val emvImpl = EMVImpl()
    val deviceKeyStorage = DeviceKeyStorage(context = context)
    val dbParams = ParametroDB(context)
    val opResponse : State<OperationResponseModel?>? = doPayment2?.operationResponse?.observeAsState()

    LaunchedEffect(key1 = state.isPaymentStarted) {
        if (state.isPaymentStarted) {

            incrementBatch(context)
            val localData = LocalData(
                batch = dbParams.getValueParam(DBDefines.batch).toLong(),
                ticket = dbParams.getValueParam(DBDefines.ticket).toLong(),
                trace = dbParams.getValueParam(DBDefines.trace).toLong(),
                terminalId = terminalId,
                acquirerId = if (viewModel.operationFlow.amount?.currency == CURRENCY_LABEL_MX) Acquirer.BANORTE.name else Acquirer.GPS.name,
                merchantId = merchantId,
                aesKey = deviceKeyStorage.getAesKeyExecute(),
                ivKey = deviceKeyStorage.getIvKeyExecute(),
                banorteKey = deviceKeyStorage.getKeyExecute(),
                customerId = customerId,
                currencyLabel = if (viewModel.operationFlow.amount?.currency == CURRENCY_LABEL_MX) CURRENCY_LABEL_MX else CURRENCY_LABEL_ARG
            )
            viewModel.operationFlow.additional_info = "TEST"
            doPayment2 = DoProcessAdquirerOperationData(
                context = context,
                localData = localData,
                version = "",
                device = emvImpl,
                storage = storage,
                transactionDate = DateUtil.getLocalDateTimeWithOffset(),
                dataFlow = viewModel.operationFlow
            )

            doPayment2?.doOperation(operationType = viewModel.operationFlow.transactionType!!)
        }
    }

    LaunchedEffect(key1 = Unit) {
        viewModel.submitAction(CardProcessAction.LogCardProcess("Inicio de lectura de tarjeta..."))
        cardProcessData.findCardProcess(
            operationFlow = viewModel.doOperationFlow(),
            cardData = viewModel.getCardData(),
            context = context,
            inputModeType = InputMode.ALL
        )

        viewModel.submitAction(CardProcessAction.LogCardProcess("Inserta tarjeta para continuar..."))
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
            viewModel.submitAction(CardProcessAction.LogCardProcess("Selecciona app.."))
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
                        viewModel.submitAction(CardProcessAction.LogCardProcess("BIN encontrado: $it"))
                        //Validar BIN
                        binValidationData.setOperationFlow(
                            operationFlow = viewModel.operationFlow,
                            merchantId = merchantId,
                            customerId = customerId,
                            currency = if (Currency.MX.name == viewModel.currency) CURRENCY_LABEL_MX else CURRENCY_LABEL_ARG,
                            interest = false
                        )
                        binValidationData.doBinValidation(it)
                    }
                }
            }
        }
    }

    LaunchedEffect(key1 = binResponse) {
        binResponse?.let {
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
                viewModel.submitAction(CardProcessAction.LogCardProcess("Tipo de tarjeta encontrada: $cardType"))
                when (cardType) {
                    // Caso de prueba solo se usara una sola cuota
//                    CardType.CREDIT.name -> {
//                        Log.i(TAG, "Mostrar pantalla de cuotas disponibles")
//                        val intent = Intent(this, InstallmentsActivity::class.java).apply {
//                            putParcelableArrayListExtra(
//                                "installment_list",
//                                ArrayList(it.installments.installments)
//                            )
//                        }
//                        startActivity(intent)
//                    }

                    else -> { //Debit y Prepaid
                        viewModel.submitAction(CardProcessAction.LogCardProcess("Redirijiendo a pago"))
                        viewModel.operationFlow.installments = "01"

                        viewModel.submitAction(CardProcessAction.LogCardProcess("Redirijiendo a pago"))
                        viewModel.submitAction(CardProcessAction.StartPaymentProcess)
                    }
                }

            } else {
                viewModel.submitAction(CardProcessAction.LogCardProcess("BIN no encontrado"))
                val brandsAvailable = it.brandsAvailable
                viewModel.submitAction(CardProcessAction.LogCardProcess("Marcas y tipos disponibles: $brandsAvailable"))

                //TODO el cliente puede construir pantallas para selección manual, tomando como input brandsAvailable. Además, debe construir el consumo de installments, ejemplo:
                viewModel.submitAction(CardProcessAction.LogCardProcess("TODO el cliente puede construir pantallas para selección manual, tomando como input brandsAvailable. Además, debe construir el consumo de installments."))
            }
        }
    }

    LaunchedEffect(key1 = opResponse?.value) {
        opResponse?.value?.let {
            viewModel.submitAction(CardProcessAction.LogCardProcess("Operation response: ${opResponse?.value?.status?.message}"))
            val operationResponse = it as Adquirer

            val gson = Gson()
            val operationResponseJson = gson.toJson(operationResponse)

            Timber.d( "operationResponseJson: $operationResponseJson")
            Timber.d( "response.code: ${operationResponse.response?.code}")
            Timber.d( "response.description: ${operationResponse.response?.description}")
            Timber.d("response.name: ${operationResponse.response?.name}")

            if (operationResponse.status?.code == OperationResponseCode.APPROVED) {
                Timber.d( "PaymentId: ${operationResponse.id}")
                Timber.d( "OperationNumber: ${operationResponse.ticketId}")
                viewModel.submitAction(CardProcessAction.LogCardProcess("Pago Exitoso!"))
            } else {
                Timber.d( "Pago declinado!")
                viewModel.submitAction(CardProcessAction.LogCardProcess("Pago Declinado!"))
            }
        }?: run {
//            viewModel.submitAction(CardProcessAction.LogCardProcess("Pago declinado por response null"))
        }
    }

    CardProcessContent(
        info = state.info
    )
}