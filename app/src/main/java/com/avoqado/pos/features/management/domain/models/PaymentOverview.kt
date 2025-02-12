package com.avoqado.pos.features.management.domain.models

data class PaymentOverview(
    val paidProducts: List<String>,
    val equalPartySize: Int,
    val equalPartyPaidSize: Int,
)
