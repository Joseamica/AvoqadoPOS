package com.avoqado.pos.features.management.domain.models

data class TableDetail(
    val id: String,
    val products: List<Product>,
    val totalPending: Double,
    val name: String,
    val totalAmount: Double,
    val waiterName: String?,
    val paymentOverview: PaymentOverview?
)
