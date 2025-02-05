package com.avoqado.pos.features.payment.data.cache.models

import java.time.LocalDateTime

data class PaymentCacheEntity (
    val tipAmount: Double,
    val subtotal: Double,
    val paymentId: String,
    val date: LocalDateTime,
    val rootData: String,
    val waiterName: String,
    val splitType: String,
    val venueId: String,
    val tableNumber: String,
    val billId: String,
    val products: List<String>
)