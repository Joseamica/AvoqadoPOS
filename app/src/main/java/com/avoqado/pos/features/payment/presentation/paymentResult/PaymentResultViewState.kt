package com.avoqado.pos.features.payment.presentation.paymentResult

import com.menta.android.core.model.Adquirer

data class PaymentResultViewState(
    val isLoading: Boolean = false,
    val paymentResult: PaymentResult? = null,
    val tipAmount: Double = 0.0,
    val subtotalAmount: Double = 0.0,
    val qrCode: String? = null,
    val adquirer: Adquirer? = null
) {
    val totalAmount: Double
        get() = tipAmount + subtotalAmount
}

enum class PaymentResult {
    SUCCESS,
    DECLINED
}