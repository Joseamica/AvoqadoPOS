package com.avoqado.pos.core.data.network.models

data class ShiftUpdateMessage(
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
    val avgTipPercentage: Int?,
    val tipsSum: Int?,
    val tipsCount: Int?,
    val paymentSum: Int?,
    val action: String?, // Puede ser "created", "updated", etc.
) 
