package com.avoqado.pos.features.payment.presentation.paymentResult

import com.avoqado.pos.core.presentation.model.OperationData
import com.avoqado.pos.core.presentation.model.OperationInfo
import com.avoqado.pos.core.presentation.model.Product
import com.avoqado.pos.core.presentation.utils.toAmountMx
import com.menta.android.core.model.Adquirer
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class PaymentResultViewState(
    val isLoading: Boolean = false,
    val paymentResult: PaymentResult? = null,
    val tipAmount: Double = 0.0,
    val subtotalAmount: Double = 0.0,
    val qrCode: String? = null,
    val adquirer: Adquirer? = null,
    val terminalSerialCode: String = "",
    val paidProducts: List<Product> = emptyList(),
) {
    val totalAmount: Double
        get() = tipAmount + subtotalAmount

    val operationInfo: OperationInfo
        get() {
            return OperationInfo(
                dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm A")),
                acquirerName = "",
                transactionId = "",
                operationData =
                    adquirer?.let {
                        OperationData(
                            cardBrand = it.capture?.card?.brand ?: "",
                            cardType = it.capture?.card?.type ?: "",
                            pan = it.capture?.card?.maskedPan ?: "",
                        )
                    },
                authOperationCode = adquirer?.authorization?.code ?: "",
                subtotal = subtotalAmount.toString().toAmountMx(),
                tip = tipAmount.toString().toAmountMx(),
            )
        }
}

enum class PaymentResult {
    SUCCESS,
    DECLINED,
}
