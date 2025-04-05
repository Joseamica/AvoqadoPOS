package com.avoqado.pos.features.management.data.model

data class ProductCacheEntity(
    val id: String,
    val name: String,
    val price: Double,
    val quantity: Double,
    val totalPrice: Double
)
