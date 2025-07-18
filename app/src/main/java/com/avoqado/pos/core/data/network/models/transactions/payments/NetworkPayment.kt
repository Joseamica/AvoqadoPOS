package com.avoqado.pos.core.data.network.models.transactions.payments

import com.google.gson.annotations.SerializedName


data class NetworkPaymentsData(
    @SerializedName("data") val data: List<NetworkPayment>,
    @SerializedName("meta") val meta: NetworkMeta
)

data class NetworkPayment(
    @SerializedName("id") val id: String,
    @SerializedName("venueId") val venueId: String,
    @SerializedName("orderId") val orderId: String?,
    @SerializedName("shiftId") val shiftId: String?,
    @SerializedName("processedById") val processedById: String?,
    @SerializedName("amount") val amount: String,
    @SerializedName("tipAmount") val tipAmount: String,
    @SerializedName("method") val method: String,
    @SerializedName("status") val status: String,
    @SerializedName("processor") val processor: String?,
    @SerializedName("processorId") val processorId: String?,
    @SerializedName("feePercentage") val feePercentage: String?,
    @SerializedName("feeAmount") val feeAmount: String?,
    @SerializedName("netAmount") val netAmount: String?,
    @SerializedName("externalId") val externalId: String?,
    @SerializedName("originSystem") val originSystem: String,
    @SerializedName("syncStatus") val syncStatus: String,
    @SerializedName("syncedAt") val syncedAt: String?,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("updatedAt") val updatedAt: String,
    @SerializedName("processedBy") val processedBy: NetworkProcessedBy?,
    @SerializedName("order") val order: NetworkOrder?,
    @SerializedName("allocations") val allocations: List<Any>?
)

data class NetworkProcessedBy(
    @SerializedName("id") val id: String,
    @SerializedName("firstName") val firstName: String?,
    @SerializedName("lastName") val lastName: String?
)

data class NetworkOrder(
    @SerializedName("id") val id: String,
    @SerializedName("table") val table: NetworkTable?
)

data class NetworkTable(
    @SerializedName("id") val id: String,
    @SerializedName("number") val number: String
)

data class NetworkMeta(
    @SerializedName("totalCount") val totalCount: Int,
    @SerializedName("pageSize") val pageSize: Int,
    @SerializedName("currentPage") val currentPage: Int,
    @SerializedName("totalPages") val totalPages: Int,
    @SerializedName("hasNextPage") val hasNextPage: Boolean,
    @SerializedName("hasPrevPage") val hasPrevPage: Boolean
)
