package com.avoqado.pos.features.payment.data.network.models

import com.google.gson.annotations.SerializedName

data class Tip(
    @SerializedName("amount")
    val amount: String,
    @SerializedName("billId")
    val billId: String?,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("paymentId")
    val paymentId: String,
    @SerializedName("percentage")
    val percentage: String,
    @SerializedName("source")
    val source: String?,
    @SerializedName("updatedAt")
    val updatedAt: String,
    @SerializedName("waiterId")
    val waiterId: String?,
)
