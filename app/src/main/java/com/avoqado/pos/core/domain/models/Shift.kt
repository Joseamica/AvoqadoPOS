package com.avoqado.pos.core.domain.models

data class Shift(
    val id: String?,
    val turnId: Int?,
    val insideTurnId: Int?,
    val origin: String?,
    val startTime: String?,
    val endTime: String?,
    val fund: String?,
    val cash: String?,
    val card: String?,
    val credit: String?,
    val cashier: String?,
    val venueId: String?,
    val updatedAt: String?,
    val createdAt: String?,
    val avgTipPercentage: Int,
    val tipsSum: Int,
    val tipsCount: Int,
    val paymentSum: Int,
) {
    val isStarted: Boolean
        get() = startTime.isNullOrBlank().not()

    val isFinished: Boolean
        get() = endTime.isNullOrBlank().not()
}
