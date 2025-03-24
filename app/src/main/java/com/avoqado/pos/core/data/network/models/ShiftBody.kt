package com.avoqado.pos.core.data.network.models

data class ShiftBody(
    val posName: String,
    val turnId: Int?,
    val endTime: String?,
    val origin: String?
)
