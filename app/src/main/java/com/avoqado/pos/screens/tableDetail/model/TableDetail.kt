package com.avoqado.pos.screens.tableDetail.model

import com.menta.android.core.utils.StringUtils

data class TableDetail(
    val tableId: String = "",
    val name: String = "",
    val products: List<Product> = emptyList(),
    val totalAmount: Float = 0.0f
) {
    val formattedTotalPrice : String
        get() = StringUtils.toStringThousandAmount(StringUtils.notFormatAmount(totalAmount.toString()))
}
