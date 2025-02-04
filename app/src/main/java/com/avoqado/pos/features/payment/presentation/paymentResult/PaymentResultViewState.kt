package com.avoqado.pos.features.payment.presentation.paymentResult

data class PaymentResultViewState(
    val isLoading: Boolean = false,
    val paymentResult: PaymentResult? = null,
    val tipAmount: Double = 0.0,
    val subtotalAmount: Double = 0.0,
    val qrCode: String? = null
) {
    val totalAmount: Double
        get() = tipAmount + subtotalAmount
}

enum class PaymentResult {
    SUCCESS,
    DECLINED
}