package com.avoqado.pos.core.domain.models

data class ShiftParams(
    val page: Int,
    val pageSize: Int,
    val venueId: String,
    val waiterIds: String? = null,
    val startTime: String? = null,
    val endTime: String? = null,
    val paymentId: String? = null,
)
