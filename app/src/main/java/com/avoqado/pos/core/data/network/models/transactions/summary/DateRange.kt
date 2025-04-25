package com.avoqado.pos.core.data.network.models.transactions.summary

import com.google.gson.annotations.SerializedName

data class DateRange(
    @SerializedName("endTime")
    val endTime: Any?,
    @SerializedName("startTime")
    val startTime: Any?,
)
