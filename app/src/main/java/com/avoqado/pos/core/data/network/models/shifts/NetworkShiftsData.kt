package com.avoqado.pos.core.data.network.models.shifts

import com.google.gson.annotations.SerializedName

data class NetworkShiftsData(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: List<NetworkShiftNew>,
    @SerializedName("meta") val meta: NetworkShiftMeta
)

data class NetworkShiftNew(
    @SerializedName("id") val id: String,
    @SerializedName("venueId") val venueId: String,
    @SerializedName("staffId") val staffId: String,
    @SerializedName("startTime") val startTime: String,
    @SerializedName("endTime") val endTime: String?,
    @SerializedName("startingCash") val startingCash: String,
    @SerializedName("endingCash") val endingCash: String?,
    @SerializedName("cashDifference") val cashDifference: String?,
    @SerializedName("totalSales") val totalSales: String,
    @SerializedName("totalTips") val totalTips: String,
    @SerializedName("totalOrders") val totalOrders: Int,
    @SerializedName("status") val status: String,
    @SerializedName("notes") val notes: String?,
    @SerializedName("originSystem") val originSystem: String,
    @SerializedName("externalId") val externalId: String?,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("updatedAt") val updatedAt: String,
    @SerializedName("staff") val staff: NetworkStaffInfo?,
    @SerializedName("orders") val orders: List<NetworkShiftOrder>?,
    @SerializedName("payments") val payments: List<NetworkShiftPayment>?
)

data class NetworkStaffInfo(
    @SerializedName("id") val id: String,
    @SerializedName("firstName") val firstName: String?,
    @SerializedName("lastName") val lastName: String?
)

data class NetworkShiftOrder(
    @SerializedName("id") val id: String,
    @SerializedName("orderNumber") val orderNumber: String?,
    @SerializedName("total") val total: String,
    @SerializedName("status") val status: String
)

data class NetworkShiftPayment(
    @SerializedName("id") val id: String,
    @SerializedName("amount") val amount: String,
    @SerializedName("tipAmount") val tipAmount: String,
    @SerializedName("method") val method: String,
    @SerializedName("status") val status: String
)

data class NetworkShiftMeta(
    @SerializedName("totalCount") val totalCount: Int,
    @SerializedName("pageSize") val pageSize: Int,
    @SerializedName("currentPage") val currentPage: Int,
    @SerializedName("totalPages") val totalPages: Int,
    @SerializedName("hasNextPage") val hasNextPage: Boolean,
    @SerializedName("hasPrevPage") val hasPrevPage: Boolean
)
