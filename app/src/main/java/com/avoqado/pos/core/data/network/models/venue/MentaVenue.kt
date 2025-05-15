package com.avoqado.pos.core.data.network.models.venue

data class MentaVenue(
    val id: String,
    val merchantIdA: String? = null,
    val apiKeyA: String? = null,
    val merchantIdB: String? = null,
    val apiKeyB: String? = null,
)
