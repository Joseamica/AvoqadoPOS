package com.avoqado.pos.views

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.avoqado.pos.Acquirer
import com.avoqado.pos.Country
import com.avoqado.pos.OperationFlowHolder
import com.avoqado.pos.doTagListMxTest
import com.avoqado.pos.doTagListTest
import com.avoqado.pos.ui.screen.CardReaderScreen
import com.menta.android.common_cross.data.datasource.local.model.Transaction
import com.menta.android.common_cross.util.CURRENCY_LABEL_MX
import com.menta.android.common_cross.util.StatusResult
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
import com.menta.android.core.utils.DateUtil.isToday
import com.menta.android.core.utils.OPERATION
import com.menta.android.core.utils.PAYMENT
import com.menta.android.core.utils.SelectApp
import com.menta.android.core.utils.StringUtils
import com.menta.android.core.utils.StringUtils.toStringThousandAmount
import com.menta.android.core.utils.TIP
import com.menta.android.core.viewmodel.CardProcessData
import com.menta.android.emv.i9100.reader.util.InputMode

class CardProcessActivity : ComponentActivity() {

    private val amount: String by lazy {
        intent.getStringExtra("amount").toString()
    }

    private val tipAmount: String by lazy {
        intent.getStringExtra("tipAmount").toString()
    }

    private val currency: String by lazy {
        intent.getStringExtra("currency").toString()
    }
    private val operationType: String by lazy {
        intent.getStringExtra("operationType").toString()
    }
    private lateinit var transaction: Transaction

    private lateinit var cardProcessData: CardProcessData
    private var operationFlow: OperationFlow = OperationFlow()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        cardProcessData = CardProcessData()
        setContent {
            val clearAmount = amount.replace(",", "").replace(".", "")
            val clearTip = tipAmount.replace(",", "").replace(".", "")
            val total = clearAmount.toInt() + clearTip.toInt()
            val formattedAmount = toStringThousandAmount(total.toString())
            val currencySymbol = "$"
            CardReaderScreen(formattedAmount, currencySymbol)
            cardReader(clearAmount, clearTip)
        }


    }

    override fun onBackPressed() {
    }

    private fun cardReader(amount: String, tipAmount: String) {
        if (operationType.isNotNull()) {
            if (operationType != "PAYMENT" && operationType != "PREAUTHORIZATION") {
                transaction = intent.getParcelableExtra("transaction")!!
                transaction.let {
                    Log.i(TAG, "Transaction ID: ${it.id}")
                }

            }
        }
        cardProcessData.selectApp.observe(this, selectAppObserver)
        cardProcessData.navigate.observe(this, navigateObserver)

        cardProcessData.findCardProcess(
            operationFlow = doOperationFlow(amount, tipAmount),
            cardData = doCardData(),
            context = this,
            inputModeType = InputMode.ALL
        )
    }

    private fun doOperationFlow(inputAmount: String, tipAmount: String): OperationFlow {
        operationFlow.amount = Amount()
        val breakdownList = Breakdown()
        breakdownList.description = OPERATION //TODO para propina se debe usar TIP
        breakdownList.amount =
            StringUtils.notFormatAmount(inputAmount) //TODO para propina SOLO se agrega el valor de la propina

        val tipBreakdown = Breakdown()
        tipBreakdown.description = TIP //TODO para propina se debe usar TIP
        tipBreakdown.amount =
            StringUtils.notFormatAmount(tipAmount) //TODO para propina SOLO se agrega el valor de la propina


        val total = inputAmount.toInt() + tipAmount.toInt()
        operationFlow.capture = Capture()
        operationFlow.capture!!.card = Card()
        operationFlow.apply {
            amount?.let {
                it.total =
                    StringUtils.notFormatAmount(total.toString()) //TODO si hay propina,se debe enviar el valor total de monto + propina
                it.currency = currency
                it.breakdown = listOf(breakdownList, tipBreakdown)
            }
        }
        if (operationType.isNotNull()) {
            if (operationType != "PAYMENT" && operationType != "PREAUTHORIZATION") {
                if (currency == CURRENCY_LABEL_MX) {
                    operationFlow.transactionType = OperationType.REFUND
                } else {
                    if (isToday(transaction.operation.datetime)) {
                        operationFlow.transactionType = OperationType.ANNULMENT
                    } else {
                        operationFlow.transactionType = OperationType.REFUND
                    }
                }
                //Se agregan identificadores del pago original
                operationFlow.acquirer_id = transaction.operation.acquirer_id
                operationFlow.payment_id = transaction.operation.id
            } else {
                when (operationType) {
                    OperationType.PAYMENT.name -> operationFlow.transactionType =
                        OperationType.PAYMENT

                    OperationType.PREAUTHORIZATION.name -> operationFlow.transactionType =
                        OperationType.PREAUTHORIZATION
                }
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

    private fun doCardData(): CardData {
        return CardData(
            countryCode = if (Currency.MX.name == currency) Country.MEX.code else Country.ARG.code,
            acquirerId = if (Currency.MX.name == currency) Acquirer.BANORTE.name else Acquirer.GPS.name,
            tagList = if (Currency.MX.name == currency) doTagListMxTest() else doTagListTest()
        )
    }

    private val selectAppObserver: (SelectApp) -> Unit = { selectApp ->
        val builder = AlertDialog.Builder(this)
        builder.setItems(selectApp.emv) { _: DialogInterface?, which: Int ->
            try {
                cardProcessData.setIndexApp(which)
                selectApp.futureTask.run()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        builder.create().show()
    }

    private val navigateObserver: (Any) -> Unit = {
        when (it) {
            is Bundle -> {
                Log.i(TAG, "Bundle: $it")
                val status = it.get("status")
                val statusResult: StatusResult = status as StatusResult
                Log.i(TAG, "statusResult: $statusResult")
                val intent = Intent(this, CardErrorActivity::class.java)
                intent.putExtra("status", statusResult)
                startActivity(intent)
            }

            is String -> {
                if (operationType == "PAYMENT" || operationType == "PREAUTHORIZATION") {
                    Log.i(TAG, "BIN de la tarjeta: $it")
                    //Validar BIN
                    val bundle = Bundle().apply {
                        putString("bin", it)
                        putString("currency", currency)
                    }
                    val intent = Intent(this, CardRulesValidationActivity::class.java).apply {
                        putExtras(bundle)
                    }
                    startActivity(intent)
                } else {
                    //Set datos adicionales de la tarjeta
                    operationFlow.capture?.card?.brand = transaction.card.brand
                    operationFlow.capture?.card?.type = transaction.card.type
                    operationFlow.capture?.card?.isInternational =
                        transaction.card.is_international
                    operationFlow.installments =
                        transaction.installment.number.toString().padStart(2, '0')
                    val intent = Intent(this, DoRefundActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    private fun String.isNotNull(): Boolean {
        return this != "null"
    }

    companion object {
        const val TAG = "CardProcessActivity"
    }
}