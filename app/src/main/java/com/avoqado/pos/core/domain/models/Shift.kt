package com.avoqado.pos.core.domain.models

import java.time.Instant

data class Shift(
    val id: String,
    val venueId: String,
    val staffId: String,
    val startTime: Instant,
    val endTime: Instant?,
    val startingCash: Double,
    val endingCash: Double?,
    val cashDifference: Double?,
    val totalSales: Double,
    val totalTips: Double,
    val totalOrders: Int,
    val status: ShiftStatus,
    val notes: String?,
    val originSystem: String,
    val externalId: String?,
    val createdAt: Instant,
    val updatedAt: Instant,
    val staff: ShiftStaff?,
    val orders: List<ShiftOrder>?,
    val payments: List<ShiftPayment>?
) {
    val isStarted: Boolean
        get() = true // If we have the shift object, it's started

    val isFinished: Boolean
        get() = endTime != null

    val duration: String
        get() {
            val end = endTime ?: Instant.now()
            val durationMillis = end.toEpochMilli() - startTime.toEpochMilli()
            val hours = durationMillis / (1000 * 60 * 60)
            val minutes = (durationMillis % (1000 * 60 * 60)) / (1000 * 60)
            return "${hours}h ${minutes}m"
        }
}

data class ShiftStaff(
    val id: String,
    val firstName: String?,
    val lastName: String?
) {
    val fullName: String
        get() = listOfNotNull(firstName, lastName).joinToString(" ").ifBlank { "N/A" }
}

data class ShiftOrder(
    val id: String,
    val orderNumber: String?,
    val total: Double,
    val status: String
)

data class ShiftPayment(
    val id: String,
    val amount: Double,
    val tipAmount: Double,
    val method: String,
    val status: String
)

enum class ShiftStatus {
    OPEN,
    CLOSED,
    PENDING
}
