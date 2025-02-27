package com.avoqado.pos.features.authorization.domain.models

data class User(
    val id: String,
    val apiKey: String,
    val venues: List<Pair<String, String>>
)
