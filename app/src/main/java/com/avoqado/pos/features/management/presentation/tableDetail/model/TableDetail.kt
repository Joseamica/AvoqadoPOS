package com.avoqado.pos.features.management.presentation.tableDetail.model

import com.avoqado.pos.core.utils.toAmountMx
import com.menta.android.core.utils.StringUtils

data class TableDetail(
    val tableId: String = "",
    val name: String = "",
    val products: List<Product> = emptyList(),
    val totalAmount: Double = 0.0,
    val totalPending: Double = 0.0
) {
    val formattedTotalPrice : String
        get() = StringUtils.notFormatAmount(totalAmount.toString()).toAmountMx()

    val formattedPendingTotalPrice: String
        get() = StringUtils.notFormatAmount(totalPending.toString()).toAmountMx()
}
