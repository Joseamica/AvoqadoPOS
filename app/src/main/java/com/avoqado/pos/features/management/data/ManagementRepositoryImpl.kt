package com.avoqado.pos.features.management.data

import com.avoqado.pos.features.management.data.cache.ManagementCacheStorage
import com.avoqado.pos.features.management.data.mapper.toCache
import com.avoqado.pos.features.management.data.mapper.toDomain
import com.avoqado.pos.features.management.domain.ManagementRepository
import com.avoqado.pos.features.management.domain.models.TableDetail

class ManagementRepositoryImpl(
    private val managementCacheStorage: ManagementCacheStorage
): ManagementRepository {
    override fun getTableBill() {
        TODO("Not yet implemented")
    }

    override fun getCachedTable(): TableDetail? {
        return managementCacheStorage.getTable()?.toDomain()
    }

    override fun setTableCache(table: TableDetail) {
        managementCacheStorage.setTable(table.toCache())
    }
}