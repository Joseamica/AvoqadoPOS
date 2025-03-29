package com.avoqado.pos.core.domain.repositories

import com.avoqado.pos.core.domain.models.Shift
import com.avoqado.pos.core.domain.models.ShiftParams
import com.avoqado.pos.core.domain.models.TerminalInfo

interface TerminalRepository {
    suspend fun getTerminalId(serialCode: String): TerminalInfo
    suspend fun getTerminalShift(venueId: String, posName: String): Shift
    suspend fun startTerminalShift(venueId: String, posName: String): Shift
    suspend fun closeTerminalShift(venueId: String, posName: String): Shift
    suspend fun getShiftSummary(params: ShiftParams): List<Shift>
}