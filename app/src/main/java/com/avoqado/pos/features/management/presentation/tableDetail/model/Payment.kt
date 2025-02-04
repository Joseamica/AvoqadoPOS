package com.avoqado.pos.features.management.presentation.tableDetail.model

data class Payment(
    val amount: Double,
    val products: List<String>,
    val splitType: String?,
    val equalPartsPayedFor: String?,
    val equalPartsPartySize: String?
)
