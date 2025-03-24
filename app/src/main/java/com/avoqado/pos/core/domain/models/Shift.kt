package com.avoqado.pos.core.domain.models

data class Shift(
    val id: String,
    val turnId: Int,
    val insideTurnId: Int,
    val origin: String,
    val startTime: String?,
    val endTime: String?,
    val fund: Int,
    val cash: Int,
    val card: Int,
    val credit: Int,
    val cashier: String,
    val venueId: String,
    val updatedAt: String,
    val createdAt: String
){
    val isStarted : Boolean
        get() = startTime.isNullOrBlank().not()

    val isFinished: Boolean
        get() = endTime.isNullOrBlank().not()
}
