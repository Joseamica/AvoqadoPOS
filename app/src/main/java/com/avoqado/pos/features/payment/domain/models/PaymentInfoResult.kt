package com.avoqado.pos.features.payment.domain.models

import java.time.LocalDateTime

data class PaymentInfoResult(
    val tipAmount: Double,
    val subtotal: Double,
    val paymentId: String,
    val date: LocalDateTime
)
