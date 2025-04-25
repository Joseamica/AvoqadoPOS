package com.avoqado.pos.features.payment.data.cache

import com.avoqado.pos.features.payment.data.cache.models.PaymentCacheEntity
import kotlinx.coroutines.flow.MutableStateFlow

class PaymentCacheStorage {
    private val paymentInfoResult = MutableStateFlow<PaymentCacheEntity?>(null)

    fun getPaymentInfo() = paymentInfoResult.value

    fun setPaymentInfo(value: PaymentCacheEntity) {
        paymentInfoResult.value = value
    }

    fun clear() {
        paymentInfoResult.value = null
    }
}
