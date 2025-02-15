package com.avoqado.pos.features.management.presentation.tableDetail.model

import com.avoqado.pos.core.domain.models.SplitType
import com.avoqado.pos.core.presentation.model.Product
import com.avoqado.pos.core.presentation.utils.toAmountMx
import com.menta.android.core.utils.StringUtils

data class TableDetail(
    val tableId: String = "",
    val name: String = "",
    val waiterName: String = "",
    val products: List<Product> = emptyList(),
    val paymentsDone: List<Payment> = emptyList(),
    val currentSplitType: SplitType? = null,
    val totalAmount: Double = 0.0,
    val billId: String = ""
) {
    val totalPending: Double
        get() = totalAmount - paymentsDone.sumOf { it.amount }

    val totalPayed: Double
        get() = paymentsDone.sumOf { it.amount }

    val formattedTotalPrice : String
        get() = StringUtils.notFormatAmount(totalAmount.toString()).toAmountMx()

    val formattedPendingTotalPrice: String
        get() = StringUtils.notFormatAmount(totalPending.toString()).toAmountMx()
}
