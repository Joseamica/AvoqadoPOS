package com.avoqado.pos

import com.avoqado.pos.features.management.data.ManagementRepositoryImpl
import com.avoqado.pos.features.management.data.cache.ManagementCacheStorage
import com.avoqado.pos.features.management.domain.ManagementRepository
import com.menta.android.core.model.OperationFlow

object OperationFlowHolder {
    var operationFlow: OperationFlow? = null
    val managementCacheStorage: ManagementCacheStorage by lazy { ManagementCacheStorage() }
    val managementRepository: ManagementRepository by lazy { ManagementRepositoryImpl(managementCacheStorage = managementCacheStorage) }
}