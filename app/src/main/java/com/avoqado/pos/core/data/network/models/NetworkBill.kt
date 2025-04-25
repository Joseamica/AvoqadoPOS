package com.avoqado.pos.core.data.network.models

import com.google.gson.annotations.SerializedName

data class NetworkBill(
    @SerializedName("billName")
    val billName: String?,
    @SerializedName("createdAt")
    val createdAt: String?,
    @SerializedName("equalPartsId")
    val equalPartsId: String?,
    @SerializedName("folio")
    val folio: String?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("key")
    val key: String?,
    @SerializedName("posOrder")
    val posOrder: Int?,
    @SerializedName("qrCode")
    val qrCode: String?,
    @SerializedName("splitFromPos")
    val splitFromPos: Boolean?,
    @SerializedName("splitType")
    val splitType: String?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("tableNumber")
    val tableNumber: Int?,
    @SerializedName("total")
    val total: String?,
    @SerializedName("updatedAt")
    val updatedAt: String?,
    @SerializedName("usertableId")
    val usertableId: String?,
    @SerializedName("venueId")
    val venueId: String?,
    @SerializedName("waiterId")
    val waiterId: String?,
    @SerializedName("waiterName")
    val waiterName: String?,
)
