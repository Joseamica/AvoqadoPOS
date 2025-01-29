package com.avoqado.pos.features.management.presentation.tableDetail.model


data class Product(
    val id: String,
    val name: String,
    val price: Double,
    val quantity: Int,
    val totalPrice: Double
) {
    val formattedPrice : String
        get() = price.toString()

    val formattedTotalPrice : String
        get() = totalPrice.toString()
}
