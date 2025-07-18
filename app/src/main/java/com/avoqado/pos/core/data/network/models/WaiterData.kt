package com.avoqado.pos.core.data.network.models

data class WaiterData(
    val id: String,
    val staffId: String,
    val venueId: String,
    val role: String,
    val permissions: Any?,
    val totalSales: String,
    val totalTips: String,
    val averageRating: String,
    val totalOrders: Int,
    val staff: StaffData,
    val venue: VenueData,
)

data class StaffData(
    val id: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String?,
    val employeeCode: String?,
    val photoUrl: String?,
    val active: Boolean,
)

data class VenueData(
    val id: String,
    val name: String,
)
