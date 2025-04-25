package com.avoqado.pos.core.data.network.models

import com.google.gson.annotations.SerializedName

data class NetworkTable(
    @SerializedName("bill")
    val bill: NetworkBill?,
    @SerializedName("billId")
    val billId: String?,
    @SerializedName("count")
    val count: Int?,
    @SerializedName("createdAt")
    val createdAt: String?,
    @SerializedName("demo")
    val demo: Boolean?,
    @SerializedName("floorId")
    val floorId: String?,
    @SerializedName("locationId")
    val locationId: String?,
    @SerializedName("seats")
    val seats: String?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("tableNumber")
    val tableNumber: Int?,
    @SerializedName("updatedAt")
    val updatedAt: String?,
    @SerializedName("venueId")
    val venueId: String?,
)
