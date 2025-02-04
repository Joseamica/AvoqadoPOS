package com.avoqado.pos.features.payment.data.cache.models

import java.time.LocalDateTime

data class PaymentCacheEntity (
    val tipAmount: Double,
    val subtotal: Double,
    val paymentId: String,
    val date: LocalDateTime
)