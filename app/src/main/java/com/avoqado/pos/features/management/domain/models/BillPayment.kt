package com.avoqado.pos.features.management.domain.models

data class BillPayment(
    val amount: Double,
    val products: List<String>,
    val splitType: String?,
    val equalPartsPayedFor: String?,
    val equalPartsPartySize: String?,
)
