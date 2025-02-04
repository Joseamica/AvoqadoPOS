package com.avoqado.pos.features.payment.data.mappers

import com.avoqado.pos.features.payment.data.cache.models.PaymentCacheEntity
import com.avoqado.pos.features.payment.domain.models.PaymentInfoResult

fun PaymentCacheEntity.toDomain(): PaymentInfoResult {
    return PaymentInfoResult(
        subtotal = this.subtotal,
        tipAmount = this.tipAmount,
        date = this.date,
        paymentId = this.paymentId,
    )
}

fun PaymentInfoResult.toCache(): PaymentCacheEntity {
    return PaymentCacheEntity(
        subtotal = this.subtotal,
        tipAmount = this.tipAmount,
        date = this.date,
        paymentId = this.paymentId,
    )
}