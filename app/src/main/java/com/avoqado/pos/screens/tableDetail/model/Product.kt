package com.avoqado.pos.screens.tableDetail.model

import com.menta.android.core.utils.StringUtils

data class Product(
    val id: String,
    val name: String,
    val price: Float,
    val quantity: Int,
    val totalPrice: Float
) {
    val formattedPrice : String
        get() = StringUtils.toStringThousandAmount(StringUtils.notFormatAmount(price.toString()))

    val formattedTotalPrice : String
        get() = StringUtils.toStringThousandAmount(StringUtils.notFormatAmount(totalPrice.toString()))
}
