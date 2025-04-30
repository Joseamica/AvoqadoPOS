package com.avoqado.pos.core.domain.mappers

import com.avoqado.pos.core.data.network.models.ShiftUpdateMessage
import com.avoqado.pos.core.domain.models.Shift

/**
 * Mapper class to convert between network and domain models
 */
object ShiftMapper {

    /**
     * Maps a network ShiftUpdateMessage to domain Shift model
     */
    fun mapToDomain(networkModel: ShiftUpdateMessage): Shift {
        return Shift(
            id = networkModel.id,
            turnId = networkModel.turnId,
            insideTurnId = networkModel.insideTurnId,
            origin = networkModel.origin,
            startTime = networkModel.startTime,
            endTime = networkModel.endTime,
            fund = networkModel.fund?.toString(),
            cash = networkModel.cash?.toString(),
            card = networkModel.card?.toString(),
            credit = networkModel.credit?.toString(),
            cashier = networkModel.cashier,
            venueId = networkModel.venueId,
            updatedAt = networkModel.updatedAt?.toString(),
            createdAt = networkModel.createdAt?.toString(),
            avgTipPercentage = 0, // Default since this is missing from network model
            tipsSum = 0,          // Default since this is missing from network model
            tipsCount = 0,        // Default since this is missing from network model
            paymentSum = networkModel.card?.toInt() ?: 0 // Assuming card is payment sum
        )
    }
} 