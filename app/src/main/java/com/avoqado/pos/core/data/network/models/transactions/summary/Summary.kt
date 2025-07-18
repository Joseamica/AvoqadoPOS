package com.avoqado.pos.core.data.network.models.transactions.summary

import com.google.gson.annotations.SerializedName

data class Summary(
    @SerializedName("averageTipPercentage")
    val averageTipPercentage: Double?,
    @SerializedName("ordersCount")
    val ordersCount: Int?,
    @SerializedName("ratingsCount")
    val ratingsCount: Int?,
    @SerializedName("totalSales")
    val totalSales: Double?,
    @SerializedName("totalTips")
    val totalTips: Double?,
)
