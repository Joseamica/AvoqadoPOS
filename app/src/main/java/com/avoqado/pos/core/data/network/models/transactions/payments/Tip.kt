package com.avoqado.pos.core.data.network.models.transactions.payments


import com.google.gson.annotations.SerializedName

data class Tip(
    @SerializedName("amount")
    val amount: String?,
    @SerializedName("billId")
    val billId: Any?,
    @SerializedName("billV2Id")
    val billV2Id: Any?,
    @SerializedName("createdAt")
    val createdAt: String?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("paymentId")
    val paymentId: String?,
    @SerializedName("percentage")
    val percentage: String?,
    @SerializedName("source")
    val source: Any?,
    @SerializedName("updatedAt")
    val updatedAt: String?,
    @SerializedName("waiterId")
    val waiterId: Any?
)