package com.avoqado.pos.core.domain.repositories

import com.avoqado.pos.core.domain.models.Payment
import com.avoqado.pos.core.domain.models.Shift
import com.avoqado.pos.core.domain.models.ShiftParams
import com.avoqado.pos.core.domain.models.ShiftSummary
import com.avoqado.pos.core.domain.models.TerminalInfo
import kotlinx.coroutines.flow.Flow

interface TerminalRepository {
    suspend fun getTerminalId(serialCode: String): TerminalInfo

    suspend fun getTerminalShift(
        venueId: String,
        posName: String,
    ): Shift

    suspend fun startTerminalShift(
        venueId: String,
        posName: String,
    ): Shift

    suspend fun closeTerminalShift(
        venueId: String,
        posName: String,
    ): Shift

    suspend fun getShiftSummary(params: ShiftParams): List<Shift>

    suspend fun getSummary(params: ShiftParams): ShiftSummary

    suspend fun getPayments(params: ShiftParams): List<Payment>

    // Nuevos métodos para WebSocket
    fun connectToShiftEvents(venueId: String)

    fun disconnectFromShiftEvents()

    fun listenForShiftEvents(): Flow<Shift>
    
    /**
     * Retrieves a payment by its ID
     * @param paymentId The unique identifier of the payment
     * @return The payment if found, null otherwise
     */
    suspend fun getPaymentById(paymentId: String): Payment?
}
