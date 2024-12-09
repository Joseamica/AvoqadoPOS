package com.avoqadoapp.screens.cardProcess

import androidx.lifecycle.SavedStateHandle
import com.avoqadoapp.Country
import com.avoqadoapp.core.OperationFlowHolder
import com.avoqadoapp.core.base.BaseViewModel
import com.avoqadoapp.doTagListMxTest
import com.avoqadoapp.doTagListTest
import com.avoqadoapp.isNotNull
import com.menta.android.common_cross.util.Acquirer
import com.menta.android.core.consts.Amount
import com.menta.android.core.consts.Breakdown
import com.menta.android.core.consts.Capture
import com.menta.android.core.consts.Card
import com.menta.android.core.consts.Holder
import com.menta.android.core.consts.Identification
import com.menta.android.core.consts.Terminal
import com.menta.android.core.model.CardData
import com.menta.android.core.model.Currency
import com.menta.android.core.model.OperationFlow
import com.menta.android.core.model.OperationType
import com.menta.android.core.utils.OPERATION
import com.menta.android.core.utils.StringUtils
import timber.log.Timber

class CardProcessViewModel constructor (
    private val savedStateHandle: SavedStateHandle
) : BaseViewModel<CardProcessViewState, CardProcessAction>(CardProcessViewState()) {

    init {
        Timber.d("Init CardProcessViewModel")
    }

    val amount: String? = savedStateHandle.get("amount")
    val currency: String? = savedStateHandle.get("currency")
    val operationType: String? = savedStateHandle.get("operationType")

    val operationFlow: OperationFlow = OperationFlow()

    val clearAmount: String
        get() = amount?.replace(",", "")?.replace(".", "") ?: ""

    override suspend fun handleActions(action: CardProcessAction) {
        when (action) {
            is CardProcessAction.LogCardProcess -> {
                updateState {
                    copy(
                        info = info.toMutableList().apply {
                            add(action.log)
                        }
                    )
                }
            }

            CardProcessAction.StartPaymentProcess -> {
                updateState {
                    copy(
                        isPaymentStarted = true
                    )
                }
            }
        }
    }

    fun doPayment(){}

    fun getCardData(): CardData {
        return CardData(
            countryCode = if (Currency.MX.name == currency) Country.MEX.code else Country.ARG.code,
            acquirerId = if (Currency.MX.name == currency) Acquirer.BANORTE.name else Acquirer.GPS.name,
            tagList = if (Currency.MX.name == currency) doTagListMxTest() else doTagListTest()
        )
    }

    fun doOperationFlow(): OperationFlow {
        operationFlow.amount = Amount()
        val breakdownList = Breakdown()
        breakdownList.description = OPERATION //TODO para propina se debe usar TIP
        breakdownList.amount =
            StringUtils.notFormatAmount(clearAmount) //TODO para propina SOLO se agrega el valor de la propina

        operationFlow.capture = Capture()
        operationFlow.capture!!.card = Card()
        operationFlow.apply {
            amount?.let {
                it.total =
                    StringUtils.notFormatAmount(clearAmount) //TODO si hay propina,se debe enviar el valor total de monto + propina
                it.currency = currency
                it.breakdown = listOf(breakdownList)
            }
        }
        if ((operationType ?: "null").isNotNull()) {
            when (operationType) {
                OperationType.PAYMENT.name -> operationFlow.transactionType =
                    OperationType.PAYMENT

                OperationType.PREAUTHORIZATION.name -> operationFlow.transactionType =
                    OperationType.PREAUTHORIZATION
            }
        } else {
            operationFlow.transactionType = OperationType.PAYMENT
        }

        //inicializar otros objetos
        operationFlow.capture!!.card.holder = Holder()
        operationFlow.capture!!.card.holder.identification = Identification()
        operationFlow.terminal = Terminal()

        OperationFlowHolder.operationFlow = operationFlow
        return operationFlow
    }
}