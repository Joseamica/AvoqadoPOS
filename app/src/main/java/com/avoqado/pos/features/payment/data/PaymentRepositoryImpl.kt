package com.avoqado.pos.features.payment.data

import com.avoqado.pos.features.payment.data.cache.PaymentCacheStorage
import com.avoqado.pos.features.payment.domain.models.PaymentInfoResult
import com.avoqado.pos.features.payment.domain.repository.PaymentRepository
import com.avoqado.pos.features.payment.data.mappers.toDomain
import com.avoqado.pos.features.payment.data.mappers.toCache

class PaymentRepositoryImpl(
    private val paymentCacheStorage: PaymentCacheStorage
) : PaymentRepository {
    override fun getCachePaymentInfo(): PaymentInfoResult? {
        return paymentCacheStorage.getPaymentInfo()?.toDomain()
    }

    override fun setCachePaymentInfo(paymentInfoResult: PaymentInfoResult) {
        paymentCacheStorage.setPaymentInfo(paymentInfoResult.toCache())
    }

    override fun clearCachePaymentInfo() {
        paymentCacheStorage.clear()
    }
}