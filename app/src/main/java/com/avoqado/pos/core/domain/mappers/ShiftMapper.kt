package com.avoqado.pos.core.domain.mappers

import com.avoqado.pos.core.data.network.models.ShiftUpdateMessage
import com.avoqado.pos.core.domain.models.Shift
import com.avoqado.pos.core.domain.models.ShiftStatus
import java.time.Instant

/**
 * Mapper class to convert between network and domain models
 */
object ShiftMapper {

    /**
     * Maps a network ShiftUpdateMessage to domain Shift model
     */
    fun mapToDomain(networkModel: ShiftUpdateMessage): Shift {
        return Shift(
            id = networkModel.id ?: "",
            venueId = networkModel.venueId ?: "",
            staffId = "", // TODO: Get actual staff ID from network model
            startTime = networkModel.startTime?.let { Instant.parse(it) } ?: Instant.now(),
            endTime = networkModel.endTime?.let { Instant.parse(it) },
            startingCash = 0.0, // TODO: Map from legacy data
            endingCash = null,
            cashDifference = null,
            totalSales = networkModel.card?.toDouble() ?: 0.0, // TODO: Map properly
            totalTips = 0.0, // TODO: Map from legacy data
            totalOrders = 0,
            status = if (networkModel.endTime.isNullOrBlank()) ShiftStatus.OPEN else ShiftStatus.CLOSED,
            notes = null,
            originSystem = networkModel.origin ?: "AVOQADO",
            externalId = null,
            createdAt = networkModel.createdAt?.let { Instant.parse(it.toString()) } ?: Instant.now(),
            updatedAt = networkModel.updatedAt?.let { Instant.parse(it.toString()) } ?: Instant.now(),
            staff = null, // TODO: Get actual staff info
            orders = null,
            payments = null
        )
    }
} 