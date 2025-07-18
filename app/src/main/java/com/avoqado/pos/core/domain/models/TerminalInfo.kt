package com.avoqado.pos.core.domain.models

data class TerminalInfo(
    val id: String,
    val serialCode: String,
    val venueId: String? = null,
    val venue: String? = null,
    val status: String? = null,
    val isActive: Boolean? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
)
