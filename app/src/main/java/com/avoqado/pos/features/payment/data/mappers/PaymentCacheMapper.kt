package com.avoqado.pos.features.payment.data.mappers

import com.avoqado.pos.core.domain.models.SplitType
import com.avoqado.pos.features.payment.data.cache.models.PaymentCacheEntity
import com.avoqado.pos.features.payment.domain.models.PaymentInfoResult

fun PaymentCacheEntity.toDomain(): PaymentInfoResult =
    PaymentInfoResult(
        subtotal = this.subtotal,
        tipAmount = this.tipAmount,
        date = this.date,
        paymentId = this.paymentId,
        rootData = this.rootData,
        waiterName = this.waiterName,
        splitType = SplitType.valueOf(splitType),
        venueId = venueId,
        tableNumber = tableNumber,
        billId = billId,
        products = products,
    )

fun PaymentInfoResult.toCache(): PaymentCacheEntity =
    PaymentCacheEntity(
        subtotal = this.subtotal,
        tipAmount = this.tipAmount,
        date = this.date,
        paymentId = this.paymentId,
        rootData = this.rootData,
        waiterName = waiterName,
        splitType = splitType?.value ?: "",
        venueId = venueId,
        tableNumber = tableNumber,
        billId = billId,
        products = products,
    )
