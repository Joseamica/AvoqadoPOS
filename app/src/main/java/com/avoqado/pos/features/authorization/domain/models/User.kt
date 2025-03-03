package com.avoqado.pos.features.authorization.domain.models

data class User(
    val id: String,
    val apiKey: String,
    val venueId: String,
    val primaryMerchantId: String,
    val secondaryMerchantId: String?,
    val secondaryApiKey: String?
)
