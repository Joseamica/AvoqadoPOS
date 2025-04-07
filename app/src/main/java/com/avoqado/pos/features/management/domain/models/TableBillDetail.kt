package com.avoqado.pos.features.management.domain.models

import com.avoqado.pos.core.domain.models.SplitType

data class TableBillDetail(
    val tableId: String = "",
    val paymentsDone: List<BillPayment> = emptyList(),
    val currentSplitType: SplitType? = null,
    val totalAmount: Double = 0.0,
    val billId: String = "",
    val id: String,
    val products: List<Product>,
    val totalPending: Double,
    val name: String,
    val waiterName: String?,
    val paymentOverview: PaymentOverview?
)
