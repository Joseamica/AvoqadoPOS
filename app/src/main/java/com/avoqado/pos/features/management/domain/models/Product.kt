package com.avoqado.pos.features.management.domain.models

data class Product(
    val id: String,
    val name: String,
    val quantity: Double,
    val price: Double
)
