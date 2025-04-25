package com.avoqado.pos.features.management.data.network.models

import com.google.gson.annotations.SerializedName

data class PaymentItemNetwork(
    @SerializedName("id")
    val id: String,
    @SerializedName("amount")
    val amount: String,
    @SerializedName("customerId")
    val customerId: String?,
    @SerializedName("receiptUrl")
    val receiptUrl: String?,
    @SerializedName("source")
    val source: String?,
    @SerializedName("method")
    val method: String?,
    @SerializedName("methodString")
    val methodString: String?,
    @SerializedName("userId")
    val userId: String?,
    @SerializedName("anonymousUser")
    val anonymousUser: String?,
    @SerializedName("products")
    val products: List<ProductItemNetwork>?,
    @SerializedName("equalPartsPayedFor")
    val equalPartsPayedFor: String?,
    @SerializedName("equalPartsPartySize")
    val equalPartsPartySize: String?,
    @SerializedName("splitType")
    val splitType: String?,
    @SerializedName("user")
    val user: String?,
)
