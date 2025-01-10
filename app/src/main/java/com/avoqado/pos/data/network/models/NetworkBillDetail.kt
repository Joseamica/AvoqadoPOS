package com.avoqado.pos.data.network.models


import com.google.gson.annotations.SerializedName

data class NetworkBillDetail(
    @SerializedName("amount_left")
    val amountLeft: Int,
    @SerializedName("billName")
    val billName: String?,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("equalParts")
    val equalParts: String?,
    @SerializedName("equalPartsId")
    val equalPartsId: String?,
    @SerializedName("folio")
    val folio: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("isAdmin")
    val isAdmin: Boolean,
    @SerializedName("key")
    val key: String,
    @SerializedName("payments")
    val payments: List<Any?>,
    @SerializedName("posOrder")
    val posOrder: Int,
    @SerializedName("products")
    val products: List<Product>,
    @SerializedName("qrCode")
    val qrCode: String?,
    @SerializedName("splitFromPos")
    val splitFromPos: Boolean,
    @SerializedName("splitType")
    val splitType: String?,
    @SerializedName("status")
    val status: String,
    @SerializedName("tableNumber")
    val tableNumber: Int,
    @SerializedName("total")
    val total: Int,
    @SerializedName("updatedAt")
    val updatedAt: String,
    @SerializedName("usertableId")
    val usertableId: String?,
    @SerializedName("venue")
    val venue: Venue,
    @SerializedName("venueId")
    val venueId: String,
    @SerializedName("waiterId")
    val waiterId: String?,
    @SerializedName("waiterName")
    val waiterName: String?
)