package com.avoqado.pos.features.management.data.model

data class TableCacheEntity(
    val id: String,
    val name: String,
    val products: List<ProductCacheEntity>,
    val totalPending: Double,
    val totalAmount: Double
)
