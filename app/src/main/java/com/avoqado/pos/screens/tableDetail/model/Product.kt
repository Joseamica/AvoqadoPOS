package com.avoqado.pos.screens.tableDetail.model

import com.menta.android.core.utils.StringUtils

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
