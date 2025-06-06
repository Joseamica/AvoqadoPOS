package com.avoqado.pos.features.payment.presentation.paymentResult

import com.avoqado.pos.core.presentation.model.OperationData
import com.avoqado.pos.core.presentation.model.OperationInfo
import com.avoqado.pos.core.presentation.model.Product
import com.avoqado.pos.core.presentation.utils.toAmountMx
import com.example.content_core_service.transaction_service.models.TransactionModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class PaymentResultViewState(
    val isLoading: Boolean = false,
    val paymentResult: PaymentResult? = null,
    val tipAmount: Double = 0.0,
    val subtotalAmount: Double = 0.0,
    val qrCode: String? = null,
    val adquirer: TransactionModel? = null,
    val terminalSerialCode: String = "",
    val paidProducts: List<Product> = emptyList(),
    val isQuickPayment: Boolean = false,
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
                            cardBrand = it.additionalInformation?.cardBrand ?: "",
                            cardType = it.additionalInformation?.cvmType ?: "",
                            pan = it.maskPan ?: "",
                        )
                    },
                authOperationCode = adquirer?.authorizationNumber ?: "",
                subtotal = subtotalAmount.toString().toAmountMx(),
                tip = tipAmount.toString().toAmountMx(),
            )
        }
}

enum class PaymentResult {
    SUCCESS,
    DECLINED,
}
