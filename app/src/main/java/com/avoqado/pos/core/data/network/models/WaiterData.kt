package com.avoqado.pos.core.data.network.models

data class WaiterData(
    val id: String,
    val idmesero: String,
    val nombre: String,
    val captain: Boolean,
    val pin: String,
    val venueId: String,
    val updatedAt: String,
    val createdAt: String,
)
