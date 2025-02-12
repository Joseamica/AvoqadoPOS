package com.avoqado.pos.features.payment.domain.models

import com.avoqado.pos.core.domain.models.SplitType
import java.time.LocalDateTime

data class PaymentInfoResult(
    val tipAmount: Double,
    val subtotal: Double,
    val paymentId: String,
    val date: LocalDateTime,
    val rootData: String,
    val splitType: SplitType?,
    val waiterName: String,
    val tableNumber: String,
    val venueId: String,
    val billId: String,
    val products: List<String> = emptyList(),
    val splitPartySize: Int = 0,
    val splitSelectedPartySize: Int = 0
)
