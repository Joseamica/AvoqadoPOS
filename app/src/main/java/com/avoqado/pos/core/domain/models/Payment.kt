package com.avoqado.pos.core.domain.models

import java.time.Instant

data class Payment(
    val id: String,
    val venueId: String,
    val orderId: String?,
    val shiftId: String?,
    val processedById: String?,
    val amount: Double,
    val tipAmount: Double,
    val method: String,
    val status: String,
    val processor: String?,
    val processorId: String?,
    val feePercentage: Double?,
    val feeAmount: Double?,
    val netAmount: Double?,
    val externalId: String?,
    val originSystem: String,
    val syncStatus: String,
    val syncedAt: Instant?,
    val createdAt: Instant,
    val updatedAt: Instant,
    val processedBy: ProcessedBy?,
    val order: Order?,
    val allocations: List<Allocation>?,
    val tableNumber: String?
)

data class ProcessedBy(
    val id: String,
    val firstName: String?,
    val lastName: String?
) {
    val fullName: String
        get() = listOfNotNull(firstName, lastName).joinToString(" ").ifBlank { "N/A" }
}

data class Order(
    val id: String
)

data class Allocation(
    val id: String,
    val amount: Double
)

