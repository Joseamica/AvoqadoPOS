package com.avoqado.pos.core.data.network.models.transactions

import com.google.gson.annotations.SerializedName

data class NetworkShiftRecord(
    @SerializedName("active")
    val active: Boolean?,
    @SerializedName("card")
    val card: String?,
    @SerializedName("cash")
    val cash: String?,
    @SerializedName("cashier")
    val cashier: String?,
    @SerializedName("createdAt")
    val createdAt: String?,
    @SerializedName("credit")
    val credit: String?,
    @SerializedName("endTime")
    val endTime: String?,
    @SerializedName("fum")
    val fum: Any?,
    @SerializedName("fund")
    val fund: String?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("insideTurnId")
    val insideTurnId: Int?,
    @SerializedName("origin")
    val origin: String?,
    @SerializedName("payments")
    val payments: List<Payment?>?,
    @SerializedName("startTime")
    val startTime: String?,
    @SerializedName("turnId")
    val turnId: Int?,
    @SerializedName("updatedAt")
    val updatedAt: String?,
    @SerializedName("venue")
    val venue: Venue?,
    @SerializedName("venueId")
    val venueId: String?,
    @SerializedName("avgTipPercentage")
    val avgTipPercentage: Double?,
    @SerializedName("tipsSum")
    val tipsSum: Int?,
    @SerializedName("tipsCount")
    val tipsCount: Int?,
    @SerializedName("paymentSum")
    val paymentSum: Int?,
)
