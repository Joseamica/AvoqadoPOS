package com.avoqado.pos.views

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.RemoteException
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Observer
import com.avoqado.pos.core.presentation.model.enums.Acquirer
import com.avoqado.pos.AppfinRestClientConfigure
import com.avoqado.pos.AvoqadoApp
import com.avoqado.pos.CURRENCY_LABEL
import com.avoqado.pos.MainActivity
import com.avoqado.pos.OperationFlowHolder
import com.avoqado.pos.R
import com.avoqado.pos.core.domain.models.SplitType
import com.avoqado.pos.core.presentation.utils.toAmountMXDouble
import com.avoqado.pos.customerId
import com.avoqado.pos.features.payment.presentation.navigation.PaymentDests
import com.avoqado.pos.merchantId
import com.avoqado.pos.terminalId
import com.avoqado.pos.ui.screen.ProcessingOperationScreen
import com.avoqado.pos.core.presentation.utils.Utils.incrementBatch
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
import com.menta.android.printer.i9100.core.DevicePrintImpl
import com.menta.android.printer.i9100.model.Align
import com.menta.android.printer.i9100.model.TextFormat
import com.menta.android.printer.i9100.util.TOTAL_LABEL
import com.menta.android.restclient.core.RestClientConfiguration
import com.menta.android.restclient.core.Storage
import java.time.LocalDateTime
import java.util.Locale


class DoPaymentActivity : ComponentActivity() {

    private val operationFlow: OperationFlow?
        get() = OperationFlowHolder.operationFlow

    private val splitType: SplitType by lazy {
        SplitType.valueOf(intent.getStringExtra("splitType").toString())
    }
    private val waiterName: String by lazy {
        intent.getStringExtra("waiterName").toString()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("${TAG}-AvoqadoTest", "New instance of ${TAG}")
        enableEdgeToEdge()
        setContent {
            ProcessingOperationScreen(
                title = stringResource(id = R.string.wait_payment),
                message = stringResource(id = R.string.whileServicePaymentProcess)
            )
        }
        RestClientConfiguration.configure(AppfinRestClientConfigure())
        val deviceKeyStorage = DeviceKeyStorage(context = this)
        val dbParams = ParametroDB(this)
        incrementBatch(this)
        val localData = LocalData(
            batch = dbParams.getValueParam(DBDefines.batch).toLong(),
            ticket = dbParams.getValueParam(DBDefines.ticket).toLong(),
            trace = dbParams.getValueParam(DBDefines.trace).toLong(),
            terminalId = terminalId,
            acquirerId = if (operationFlow?.amount?.currency == CURRENCY_LABEL_MX) Acquirer.BANORTE.name else Acquirer.GPS.name,
            merchantId = merchantId,
            aesKey = deviceKeyStorage.getAesKeyExecute(),
            ivKey = deviceKeyStorage.getIvKeyExecute(),
            banorteKey = deviceKeyStorage.getKeyExecute(),
            customerId = customerId,
            currencyLabel = if (operationFlow?.amount?.currency == CURRENCY_LABEL_MX) CURRENCY_LABEL_MX else CURRENCY_LABEL_ARG
        )
        operationFlow?.additional_info = "TEST"
        val emvImpl = EMVImpl()
        val storage = Storage(context = this)
        val doPayment = DoProcessAdquirerOperationData(
            context = this,
            localData = localData,
            version = "",
            device = emvImpl,
            storage = storage,
            transactionDate = DateUtil.getLocalDateTimeWithOffset(),
            dataFlow = operationFlow!!
        )

        doPayment.doOperation(operationType = operationFlow?.transactionType!!)
        doPayment.operationResponse.observe(this) {
            Log.i(TAG, "Resultado del pago: $it")
            Log.i(TAG, "Resultado: ${it.status.message}")
            it.data?.let { response ->
                val operationResponse = response as Adquirer

                val gson = Gson()
                val operationResponseJson = gson.toJson(operationResponse)

                Log.i(TAG, "operationResponseJson: $operationResponseJson")
                Log.i(TAG, "response.code: ${operationResponse.response?.code}")
                Log.i(TAG, "response.description: ${operationResponse.response?.description}")
                Log.i(TAG, "response.name: ${operationResponse.response?.name}")

                if (operationResponse.status?.code == OperationResponseCode.APPROVED) {
                    Log.i(TAG, "PaymentId: ${operationResponse.id}")
                    Log.i(TAG, "OperationNumber: ${operationResponse.ticketId}")

                    AvoqadoApp.paymentRepository.getCachePaymentInfo()?.let { info ->
                        AvoqadoApp.paymentRepository.setCachePaymentInfo(
                            paymentInfoResult = info.copy(
                                paymentId = operationResponse.ticketId.toString(),
                                tipAmount = operationResponse.amount.breakdown.firstOrNull { breakdown ->  breakdown.description == "TIP" }?.amount?.toAmountMXDouble() ?: 0.0,
                                subtotal = operationResponse.amount.breakdown.firstOrNull { breakdown ->  breakdown.description == "OPERATION" }?.amount?.toAmountMXDouble() ?: 0.0,
                                date = LocalDateTime.now(),
                                rootData = operationResponseJson,
                                splitType = splitType,
                                waiterName = waiterName
                            )
                        )
                    }

                    val intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    intent.putExtra("navigate_to", PaymentDests.PaymentResult.route)
                    startActivity(intent)
                } else {
                    Log.i(TAG, "Pago declinado!")
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

    fun printPayment(payment: Adquirer){
        Log.i("", "Probar impresora")
        val devicePrintImpl = DevicePrintImpl(context = applicationContext)
        val status = devicePrintImpl.getStatus()
        Log.i(TAG, "status impresora: $status")
        if (status == 0) {
            val thread = Thread {
                devicePrintImpl.addLine(
                    TextFormat(align = Align.CENTER, bold = true, font = 1),
                    "Madre Cafecito"
                )
                devicePrintImpl.addLine(
                    TextFormat(align = Align.CENTER, bold = false),
                    "Guanajuato 115, Roma Nte., Cuauhtémoc"
                )

                devicePrintImpl.addLinebreak(1)
                devicePrintImpl.addLine(
                    TextFormat(align = Align.CENTER, bold = true),
                    "Pago con Tarjeta de Crédito"
                )

                devicePrintImpl.addLinebreak(1)
                devicePrintImpl.addDoubleColumnText(
                    TextFormat(),
                    "Número de Operación",
                    payment.ticketId.toString()
                )
                devicePrintImpl.addDoubleColumnText(
                    TextFormat(),
                    "Tarj: ${payment.capture?.card?.maskedPan}",
                    payment.capture?.card?.brand?: ""
                )
                devicePrintImpl.addDoubleColumnText(
                    TextFormat(),
                    "CONTACTLESS",
                    ""
                )
                devicePrintImpl.addLinebreak(1)
                try {
                    devicePrintImpl.addImage(
                        getBitmap(
                            R.drawable.line,
                            applicationContext
                        )
                    )
                } catch (e: RemoteException) {
                    e.printStackTrace()
                }

                devicePrintImpl.addDoubleColumnText(
                    TextFormat(bold = true, font = 1),
                    TOTAL_LABEL.uppercase(Locale.getDefault()),
                    "%.2f".format(payment.amount.total?.toDouble() ?: 0.0)
                )

                devicePrintImpl.addDoubleColumnText(
                    TextFormat(),
                    CURRENCY_LABEL,
                    CURRENCY_LABEL
                )
                devicePrintImpl.addLinebreak(1)

                try {
                    devicePrintImpl.startPrint()
                    Handler(Looper.getMainLooper()).post {
                        devicePrintImpl.result.observeForever(resultObserver)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            thread.start()
        }
    }

    private fun getBitmap(drawableRes: Int, context: Context): Bitmap {
        val drawable = AppCompatResources.getDrawable(context, drawableRes)
        val canvas = Canvas()
        val bitmap = Bitmap.createBitmap(
            drawable!!.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        canvas.setBitmap(bitmap)
        drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        drawable.draw(canvas)
        return bitmap
    }

    private val resultObserver = Observer<Int> { result ->
        if (result == 0) {
            Log.i(TAG, "Impresión exitosa")
        } else {
            Log.i(TAG, "Error de impresión: $result")
        }
    }


    override fun onBackPressed() {
    }

    companion object {
        const val TAG = "DoPaymentActivity"
    }
}