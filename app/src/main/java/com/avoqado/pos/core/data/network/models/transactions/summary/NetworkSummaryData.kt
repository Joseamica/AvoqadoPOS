package com.avoqado.pos.core.data.network.models.transactions.summary


import com.google.gson.annotations.SerializedName

data class NetworkSummaryData(
    @SerializedName("data")
    val data: Data?,
    @SerializedName("success")
    val success: Boolean?
)