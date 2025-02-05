package com.avoqado.pos.features.payment.data.network.models


import com.google.gson.annotations.SerializedName

data class Bill(
    @SerializedName("billName")
    val billName: Any?,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("equalPartsId")
    val equalPartsId: Any?,
    @SerializedName("folio")
    val folio: Any?,
    @SerializedName("id")
    val id: String,
    @SerializedName("key")
    val key: Any?,
    @SerializedName("posOrder")
    val posOrder: Int,
    @SerializedName("products")
    val products: List<Product>,
    @SerializedName("qrCode")
    val qrCode: Any?,
    @SerializedName("splitFromPos")
    val splitFromPos: Boolean,
    @SerializedName("splitType")
    val splitType: Any?,
    @SerializedName("status")
    val status: String,
    @SerializedName("tableNumber")
    val tableNumber: Int,
    @SerializedName("total")
    val total: String,
    @SerializedName("updatedAt")
    val updatedAt: String,
    @SerializedName("usertableId")
    val usertableId: Any?,
    @SerializedName("venueId")
    val venueId: String,
    @SerializedName("waiterId")
    val waiterId: Any?,
    @SerializedName("waiterName")
    val waiterName: Any?
)