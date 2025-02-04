package com.avoqado.pos.features.payment.domain.repository

import com.avoqado.pos.features.payment.domain.models.PaymentInfoResult

interface PaymentRepository {
    fun getCachePaymentInfo(): PaymentInfoResult?
    fun setCachePaymentInfo(paymentInfoResult: PaymentInfoResult)
    fun clearCachePaymentInfo()
}