package com.avoqado.pos.core.domain.models

// Make sure your PaymentUpdate class accepts nullable fields
data class PaymentUpdate(
    val amount: Double = 0.0,
    val splitType: SplitType = SplitType.EQUALPARTS,
    val venueId: String = "",
    val tableNumber: Int = 0,
    val method: String = "",
    val status: String? = null,  // Nullable to handle cases where status isn't provided
    val billId: String? = null,
    val equalPartsPartySize: String? = null,
    val equalPartsPayedFor: String? = null
)