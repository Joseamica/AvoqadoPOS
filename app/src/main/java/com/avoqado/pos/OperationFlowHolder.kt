package com.avoqado.pos

import com.avoqado.pos.core.data.network.AvoqadoAPI
import com.avoqado.pos.features.management.data.ManagementRepositoryImpl
import com.avoqado.pos.features.management.data.cache.ManagementCacheStorage
import com.avoqado.pos.features.management.domain.ManagementRepository
import com.avoqado.pos.features.payment.data.PaymentRepositoryImpl
import com.avoqado.pos.features.payment.data.cache.PaymentCacheStorage
import com.avoqado.pos.features.payment.data.network.AvoqadoService
import com.avoqado.pos.features.payment.domain.repository.PaymentRepository
import com.menta.android.core.model.OperationFlow

object OperationFlowHolder {
    var operationFlow: OperationFlow? = null
    //TODO move this to DI
    val managementCacheStorage: ManagementCacheStorage by lazy { ManagementCacheStorage() }
    val managementRepository: ManagementRepository by lazy { ManagementRepositoryImpl(managementCacheStorage = managementCacheStorage) }
    val PaymentCacheStorage: PaymentCacheStorage by lazy { PaymentCacheStorage() }
    val paymentRepository: PaymentRepository by lazy {
        PaymentRepositoryImpl(
            paymentCacheStorage = PaymentCacheStorage,
            avoqadoService = AvoqadoAPI.retrofit.create(AvoqadoService::class.java)
        )
    }
}