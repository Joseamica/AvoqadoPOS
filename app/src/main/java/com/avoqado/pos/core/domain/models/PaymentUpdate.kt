package com.avoqado.pos.core.domain.models

data class PaymentUpdate(
    val amount: Double,
    val splitType: SplitType,
    val venueId: String,
    val tableNumber: Int,
    val method: String,
)
