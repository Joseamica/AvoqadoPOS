package com.avoqado.pos.features.management.data

import com.avoqado.pos.core.data.network.AvoqadoService
import com.avoqado.pos.core.data.network.SocketIOManager
import com.avoqado.pos.core.data.network.models.NetworkVenue
import com.avoqado.pos.core.domain.models.AvoqadoError
import com.avoqado.pos.core.domain.models.PaymentUpdate
import com.avoqado.pos.core.domain.models.SplitType
import com.avoqado.pos.features.management.data.cache.ManagementCacheStorage
import com.avoqado.pos.features.management.data.mapper.toCache
import com.avoqado.pos.features.management.data.mapper.toDomain
import com.avoqado.pos.features.management.domain.ManagementRepository
import com.avoqado.pos.features.management.domain.models.TableDetail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException

class ManagementRepositoryImpl(
    private val managementCacheStorage: ManagementCacheStorage,
    private val avoqadoService: AvoqadoService
): ManagementRepository {

    override suspend fun getTableDetail(tableNumber: String, venueId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun getTableBill(tableBillId: String) {
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

    override fun listenTableEvents(): Flow<PaymentUpdate> {
        return SocketIOManager.messageFlow.map {
            PaymentUpdate(
                amount = (it.amount?.toDoubleOrNull() ?: 0.0) / 100.0,
                splitType = SplitType.valueOf(it.splitType ?: ""),
                venueId = it.venueId ?: "",
                tableNumber = it.tableNumber ?: 0,
                method = it.method ?: ""
            )
        }
    }

    override fun stopListeningTableEvents() {
        SocketIOManager.unsubscribe()
        SocketIOManager.disconnect()
    }

    override suspend fun getVenue(venueId: String): NetworkVenue {
        return try {
            avoqadoService.getVenueDetail(venueId)
        } catch (e: Exception) {
            if (e is HttpException) {
                if (e.code() == 401) {
                    throw AvoqadoError.Unauthorized
                } else {
                    throw AvoqadoError.BasicError(message = e.message())
                }
            } else {
                throw AvoqadoError.BasicError(message = "Algo salio mal...")
            }
        }
    }
}