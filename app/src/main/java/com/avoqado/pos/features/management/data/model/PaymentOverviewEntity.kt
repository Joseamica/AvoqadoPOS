package com.avoqado.pos.features.management.data.model

data class PaymentOverviewEntity(
    val paidProducts: List<String>,
    val equalPartySize: Int,
    val equalPartyPaidSize: Int,
)
