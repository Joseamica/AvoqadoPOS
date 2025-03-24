package com.avoqado.pos.core.domain.repositories

import com.avoqado.pos.core.domain.models.TerminalInfo

interface TerminalRepository {
    suspend fun getTerminalId(serialCode: String): TerminalInfo
    suspend fun getTerminalShift(venueId: String): String
}