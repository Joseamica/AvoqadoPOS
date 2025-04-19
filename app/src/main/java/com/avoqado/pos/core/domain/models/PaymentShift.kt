package com.avoqado.pos.core.domain.models

import java.time.Instant

data class PaymentShift(
    val id: String,
    val waiterName: String,
    val totalSales: Int,
    val totalTip: Int,
    val paymentId: String,
    val date: Instant,
    val createdAt: Instant
)
