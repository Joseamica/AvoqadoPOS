package com.avoqado.pos.core.domain.models

enum class PaymentStatus(
    val value: String,
) {
    ACCEPTED("ACCEPTED"),
    REJECTED("REJECTED"),
    PENDING("PENDING"),
    REFUNDED("REFUNDED"),
}
