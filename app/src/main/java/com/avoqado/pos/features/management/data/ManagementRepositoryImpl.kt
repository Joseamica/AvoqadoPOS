package com.avoqado.pos.features.management.data

import com.avoqado.pos.core.data.network.SocketIOManager
import com.avoqado.pos.features.management.data.cache.ManagementCacheStorage
import com.avoqado.pos.features.management.data.mapper.toCache
import com.avoqado.pos.features.management.data.mapper.toDomain
import com.avoqado.pos.features.management.domain.ManagementRepository
import com.avoqado.pos.features.management.domain.models.TableDetail
import kotlinx.coroutines.flow.Flow

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

    override fun connectToTableEvents(venueId: String, tableId: String) {
        SocketIOManager.connect("https://api.avoqado.io")
        SocketIOManager.subscribeToTable(venueId, tableId)
    }

    override fun listenTableEvents(): Flow<String> {
        return SocketIOManager.messageFlow
    }

    override fun stopListeningTableEvents() {
        SocketIOManager.unsubscribe()
        SocketIOManager.disconnect()
    }
}