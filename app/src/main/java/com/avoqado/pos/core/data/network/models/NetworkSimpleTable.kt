package com.avoqado.pos.core.data.network.models

import com.google.gson.annotations.SerializedName

data class NetworkSimpleTable(
    @SerializedName("billId")
    val billId: String?,
    @SerializedName("count")
    val count: Int?,
    @SerializedName("createdAt")
    val createdAt: String?,
    @SerializedName("demo")
    val demo: Boolean?,
    @SerializedName("floorId")
    val floorId: Any?,
    @SerializedName("locationId")
    val locationId: Any?,
    @SerializedName("seats")
    val seats: Any?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("tableNumber")
    val tableNumber: Int?,
    @SerializedName("updatedAt")
    val updatedAt: String?,
    @SerializedName("venueId")
    val venueId: String?,
)
