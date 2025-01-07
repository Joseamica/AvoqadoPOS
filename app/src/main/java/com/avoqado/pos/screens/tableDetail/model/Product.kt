package com.avoqado.pos.screens.tableDetail.model

import com.menta.android.core.utils.StringUtils

data class Product(
    val id: String,
    val name: String,
    val price: Double,
    val quantity: Int
) {
    val formattedPrice : String
        get() = StringUtils.toStringThousandAmount(StringUtils.notFormatAmount(price.toString()))

    val totalPrice: Double = quantity * price

    val formattedTotalPrice : String
        get() = StringUtils.toStringThousandAmount(StringUtils.notFormatAmount(totalPrice.toString()))
}
