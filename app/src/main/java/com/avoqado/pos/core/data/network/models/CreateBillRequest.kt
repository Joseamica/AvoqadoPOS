package com.avoqado.pos.core.data.network.models

import com.google.gson.annotations.SerializedName

/**
 * Request body for creating a new bill
 */
data class CreateBillRequest(
    @SerializedName("tableName") val tableName: String,
    @SerializedName("venueId") val venueId: String,
    @SerializedName("status") val status: String = "OPEN",
    @SerializedName("waiterName") val waiterName: String = "",
    @SerializedName("waiterId") val waiterId: String = "",
    @SerializedName("products") val products: List<String> = emptyList(),
    @SerializedName("total") val total: String = "0"
)
