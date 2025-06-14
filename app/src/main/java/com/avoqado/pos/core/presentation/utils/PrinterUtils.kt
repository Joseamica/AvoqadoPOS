package com.avoqado.pos.core.presentation.utils

import android.content.Context
import android.os.Bundle
import android.os.RemoteException
import com.avoqado.pos.CURRENCY_LABEL
import com.avoqado.pos.R
import com.avoqado.pos.core.presentation.model.OperationInfo
import com.avoqado.pos.core.presentation.model.Product
import com.avoqado.pos.core.presentation.model.TableInfo
import com.avoqado.pos.core.presentation.model.VenueInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object PrinterUtils {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    fun printOrderReceipt(
        context: Context,
        venue: VenueInfo,
        terminalSerialCode: String,
        operationInfo: OperationInfo,
        qrInfo: String? = null,
        products: List<Product> = emptyList(),
    ) {
//        val devicePrintImpl = DevicePrintImpl(context)
//        val status = devicePrintImpl.getStatus()
//        if (status == 0) {
//            scope.launch {
//                devicePrintImpl.addHeading(context, venue = venue)
//
//                operationInfo.operationData?.let {
//                    devicePrintImpl.addLine(
//                        TextFormat(align = Align.CENTER, bold = true),
//                        "Pago con Tarjeta de Crédito",
//                    )
//
//                    devicePrintImpl.addLinebreak(1)
//
//                    devicePrintImpl.addLine(
//                        TextFormat(align = Align.CENTER, bold = true),
//                        "Autorización: ${operationInfo.authOperationCode}",
//                    )
//
//                    devicePrintImpl.addDoubleColumnText(
//                        TextFormat(align = Align.CENTER, bold = false),
//                        "Terminal",
//                        terminalSerialCode,
//                    )
//
//                    devicePrintImpl.addDoubleColumnText(
//                        TextFormat(),
//                        "Número de Operación",
//                        operationInfo.transactionId,
//                    )
//
//                    devicePrintImpl.addDoubleColumnText(
//                        TextFormat(),
//                        "Tarj: ${it.pan}",
//                        it.cardBrand,
//                    )
//                    devicePrintImpl.addDoubleColumnText(
//                        TextFormat(),
//                        "CONTACTLESS",
//                        "",
//                    )
//
//                    devicePrintImpl.addDoubleColumnText(
//                        TextFormat(),
//                        "Moneda",
//                        CURRENCY_LABEL,
//                    )
//
//                    devicePrintImpl.addLinebreak(1)
//
//                    try {
//                        devicePrintImpl.addImage(
//                            context.getBitmap(
//                                R.drawable.line,
//                            ),
//                        )
//                    } catch (e: RemoteException) {
//                        e.printStackTrace()
//                    }
//                }
//
//                devicePrintImpl.addTripleColumnText(
//                    TextFormat(bold = true, font = 1),
//                    "U.",
//                    "Producto",
//                    "Monto",
//                )
//
//                products.forEach { product ->
//                    devicePrintImpl.addTripleColumnText(
//                        TextFormat(bold = false, font = 1),
//                        product.quantity.toString(),
//                        product.name,
//                        "\$${product.totalPrice.toString().toAmountMx()}",
//                    )
//                }
//
//                devicePrintImpl.addLinebreak(1)
//
//                try {
//                    devicePrintImpl.addImage(
//                        context.getBitmap(
//                            R.drawable.line,
//                        ),
//                    )
//                } catch (e: RemoteException) {
//                    e.printStackTrace()
//                }
//
//                devicePrintImpl.addDoubleColumnText(
//                    TextFormat(bold = true, font = 1),
//                    "SUBTOTAL",
//                    operationInfo.subtotal,
//                )
//
//                devicePrintImpl.addDoubleColumnText(
//                    TextFormat(bold = true, font = 1),
//                    TIP_LABEL.uppercase(Locale.getDefault()),
//                    operationInfo.tip,
//                )
//
//                devicePrintImpl.addLinebreak(1)
//
//                devicePrintImpl.addDoubleColumnText(
//                    TextFormat(bold = true, font = 1),
//                    TOTAL_LABEL.uppercase(Locale.getDefault()),
//                    operationInfo.total,
//                )
//
//                devicePrintImpl.addLinebreak(1)
//
//                qrInfo?.let {
//                    devicePrintImpl.mPrintManager.addQrCode(
//                        Bundle().apply {
//                            putInt("height", 200)
//                            putInt("align", 1)
//                        },
//                        it,
//                    )
//                }
//
//                try {
//                    withContext(Dispatchers.IO) {
//                        devicePrintImpl.startPrint()
//                    }
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//            }
//        }
    }

    fun printComanda(
        context: Context,
        venue: VenueInfo,
        tableInfo: TableInfo,
        products: List<Product>,
    ) {
//        val devicePrintImpl = DevicePrintImpl(context)
//        val status = devicePrintImpl.getStatus()
//        if (status == 0) {
//            scope.launch {
//                devicePrintImpl.addHeading(context, venue = venue)
//
//                devicePrintImpl.addLine(
//                    TextFormat(bold = false, font = 1),
//                    "MESA: ${tableInfo.name}",
//                )
//
//                devicePrintImpl.addLine(
//                    TextFormat(bold = false, font = 1),
//                    "MESERO: ${tableInfo.waiterName.uppercase()}",
//                )
//
//                devicePrintImpl.addLine(
//                    TextFormat(bold = false, font = 1),
//                    "ORDEN: ${tableInfo.orderNumber.uppercase()}",
//                )
//
//                devicePrintImpl.addLine(
//                    TextFormat(bold = false, font = 1),
//                    "FOLIO: ${tableInfo.folio.uppercase()}",
//                )
//
//                devicePrintImpl.addLine(
//                    TextFormat(bold = false, font = 1),
//                    tableInfo.timestamp.uppercase(),
//                )
//
//                devicePrintImpl.addLinebreak(1)
//
//                try {
//                    devicePrintImpl.addImage(
//                        context.getBitmap(
//                            R.drawable.line,
//                        ),
//                    )
//                } catch (e: RemoteException) {
//                    e.printStackTrace()
//                }
//
//                devicePrintImpl.addTripleColumnText(
//                    TextFormat(bold = true, font = 1),
//                    "CANT.",
//                    "DESCRIPCION",
//                    "IMPORTE",
//                )
//
//                products.forEach { product ->
//                    devicePrintImpl.addTripleColumnText(
//                        TextFormat(bold = false, font = 1),
//                        product.quantity.toString(),
//                        product.name,
//                        "\$${product.totalPrice.toString().toAmountMx()}",
//                    )
//                }
//
//                devicePrintImpl.addLinebreak(1)
//
//                devicePrintImpl.addDoubleColumnText(
//                    TextFormat(bold = true, font = 1),
//                    TOTAL_LABEL.uppercase(Locale.getDefault()),
//                    products.sumOf { it.totalPrice }.toString().toAmountMx(),
//                )
//
//                devicePrintImpl.addLinebreak(1)
//
//                devicePrintImpl.addLine(
//                    TextFormat(align = Align.CENTER, bold = false),
//                    "ESTE NO ES UN COMPROBANTE FISCAL",
//                )
//                devicePrintImpl.addLine(
//                    TextFormat(align = Align.CENTER, bold = false),
//                    "PROPINA NO INCLUIDA",
//                )
//
//                devicePrintImpl.addLinebreak(1)
//
//                try {
//                    withContext(Dispatchers.IO) {
//                        devicePrintImpl.startPrint()
//                    }
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//            }
//        }
    }

    fun printPaymentsSummary(
        context: Context,
        shiftPayments: List<Map<String, Any>>,
        venue: VenueInfo,
    ) {
//        val dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")
//
//        val devicePrintImpl = DevicePrintImpl(context)
//        val status = devicePrintImpl.getStatus()
//        if (status == 0) {
//            scope.launch {
//                devicePrintImpl.addHeading(context, venue = venue)
//
//                devicePrintImpl.addLine(
//                    TextFormat(),
//                    "PAGOS",
//                )
//
//                devicePrintImpl.addLinebreak(1)
//                try {
//                    devicePrintImpl.addImage(
//                        context.getBitmap(
//                            R.drawable.line,
//                        ),
//                    )
//                } catch (e: RemoteException) {
//                    e.printStackTrace()
//                }
//
//                devicePrintImpl.addLinebreak(1)
//
//                shiftPayments.forEach { payment ->
//                    val amount = payment["amount"] as? Double ?: 0.0
//                    val tip = payment["tip"] as? Double ?: 0.0
//                    val folio = payment["folio"] as? String ?: "-"
//                    val dateTime = payment["dateTime"] as? LocalDateTime ?: LocalDateTime.now()
//
//                    devicePrintImpl.addLine(TextFormat(), "#$folio")
//                    devicePrintImpl.addLine(TextFormat(), dateTime.format(dateFormatter))
//
//                    devicePrintImpl.addDoubleColumnText(
//                        textFormat = TextFormat(),
//                        leftText = "Venta",
//                        rightText = "\$${"%,.2f".format(amount)}",
//                    )
//
//                    devicePrintImpl.addDoubleColumnText(
//                        textFormat = TextFormat(),
//                        leftText = "Propina",
//                        rightText = "\$${"%,.2f".format(tip)}",
//                    )
//
//                    devicePrintImpl.addLinebreak(1)
//                }
//
//                devicePrintImpl.addLinebreak(1)
//
//                try {
//                    withContext(Dispatchers.IO) {
//                        devicePrintImpl.startPrint()
//                    }
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//            }
//        }
    }

    fun printShiftsSummary(
        context: Context,
        shifts: List<Map<String, Any?>>,
        venue: VenueInfo,
    ) {
//        val dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")
//
//        val devicePrintImpl = DevicePrintImpl(context)
//        val status = devicePrintImpl.getStatus()
//        if (status == 0) {
//            scope.launch {
//                devicePrintImpl.addHeading(context, venue = venue)
//
//                devicePrintImpl.addLine(
//                    TextFormat(),
//                    "TURNOS",
//                )
//
//                devicePrintImpl.addLinebreak(1)
//                try {
//                    devicePrintImpl.addImage(
//                        context.getBitmap(
//                            R.drawable.line,
//                        ),
//                    )
//                } catch (e: RemoteException) {
//                    e.printStackTrace()
//                }
//
//                devicePrintImpl.addLinebreak(1)
//
//                shifts.forEach { payment ->
//                    val amount = payment["amount"] as? Double ?: 0.0
//                    val tip = payment["tip"] as? Double ?: 0.0
//                    val folio = payment["shift"] as? String ?: "-"
//                    val startTime = payment["startTime"] as? LocalDateTime
//                    val endTime = payment["endTime"] as? LocalDateTime
//
//                    devicePrintImpl.addDoubleColumnText(TextFormat(), "Turno", folio)
//                    devicePrintImpl.addDoubleColumnText(
//                        TextFormat(),
//                        startTime?.format(dateFormatter) ?: "-",
//                        endTime?.format(dateFormatter) ?: "-",
//                    )
//
//                    devicePrintImpl.addDoubleColumnText(
//                        textFormat = TextFormat(),
//                        leftText = "Venta",
//                        rightText = "\$${"%,.2f".format(amount)}",
//                    )
//
//                    devicePrintImpl.addDoubleColumnText(
//                        textFormat = TextFormat(),
//                        leftText = "Propina",
//                        rightText = "\$${"%,.2f".format(tip)}",
//                    )
//
//                    devicePrintImpl.addLinebreak(1)
//                }
//
//                devicePrintImpl.addLinebreak(1)
//
//                try {
//                    withContext(Dispatchers.IO) {
//                        devicePrintImpl.startPrint()
//                    }
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//            }
//        }
    }

    fun printPeriodSummary(
        context: Context,
        venue: VenueInfo,
        totalSales: Double,
        totalTips: Double,
        orderCount: Int,
        ratingCount: Int,
        avgTipPercentage: Double,
        tipsByUser: List<Map<String, Any>>,
    ) {
//        val dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")
//
//        val devicePrintImpl = DevicePrintImpl(context)
//        val status = devicePrintImpl.getStatus()
//        if (status == 0) {
//            scope.launch {
//                devicePrintImpl.addHeading(context, venue = venue)
//
//                devicePrintImpl.addLine(
//                    TextFormat(),
//                    "Resumen del periodo",
//                )
//
//                devicePrintImpl.addLinebreak(1)
//                try {
//                    devicePrintImpl.addImage(
//                        context.getBitmap(
//                            R.drawable.line,
//                        ),
//                    )
//                } catch (e: RemoteException) {
//                    e.printStackTrace()
//                }
//
//                devicePrintImpl.addLinebreak(1)
//
//                devicePrintImpl.addDoubleColumnText(
//                    textFormat = TextFormat(),
//                    leftText = "Ventas",
//                    rightText = "\$${"%,.2f".format(totalSales)}",
//                )
//
//                devicePrintImpl.addDoubleColumnText(
//                    textFormat = TextFormat(),
//                    leftText = "Propinas",
//                    rightText = "\$${"%,.2f".format(totalTips)}",
//                )
//
//                devicePrintImpl.addDoubleColumnText(
//                    textFormat = TextFormat(),
//                    leftText = "Ordenes",
//                    rightText = "$orderCount",
//                )
//
//                devicePrintImpl.addDoubleColumnText(
//                    textFormat = TextFormat(),
//                    leftText = "Calificaciones",
//                    rightText = "$ratingCount",
//                )
//
//                devicePrintImpl.addDoubleColumnText(
//                    textFormat = TextFormat(),
//                    leftText = "Prom. Propinas",
//                    rightText = "${"%.2f".format(avgTipPercentage)}%",
//                )
//
//                devicePrintImpl.addLinebreak(1)
//
//                devicePrintImpl.addLine(format = TextFormat(), "Propinas por mesero")
//
//                devicePrintImpl.addLinebreak(1)
//
//                devicePrintImpl.addDoubleColumnText(TextFormat(), "Nombre", "Propina")
//
//                tipsByUser.forEach { entry ->
//                    val name = entry["name"] as? String ?: "-"
//                    val tip = entry["tip"] as? Double ?: 0.0
//                    devicePrintImpl.addDoubleColumnText(TextFormat(), name, tip.toString())
//                }
//
//                try {
//                    withContext(Dispatchers.IO) {
//                        devicePrintImpl.startPrint()
//                    }
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//            }
//        }
    }
}

//suspend fun DevicePrintImpl.addHeading(
//    context: Context,
//    venue: VenueInfo,
//) {
//    this.addLine(
//        TextFormat(align = Align.CENTER, bold = true, font = 1),
//        venue.name,
//    )
//
//    this.addLine(
//        TextFormat(align = Align.CENTER, bold = false),
//        venue.address,
//    )
//
//    this.addLine(
//        TextFormat(align = Align.CENTER, bold = false),
//        "AFILIACION: ${venue.acquisition}",
//    )
//
//    this.addLine(
//        TextFormat(align = Align.CENTER, bold = false),
//        "TEL: ${venue.phone}",
//    )
//
//    this.addLinebreak(1)
//
//    try {
//        this.addImage(
//            context.getBitmap(
//                R.drawable.line,
//            ),
//        )
//    } catch (e: RemoteException) {
//        e.printStackTrace()
//    }
//}
