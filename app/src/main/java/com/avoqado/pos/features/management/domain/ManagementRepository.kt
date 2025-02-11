package com.avoqado.pos.features.management.domain

import com.avoqado.pos.core.domain.models.PaymentUpdate
import com.avoqado.pos.features.management.domain.models.TableDetail
import kotlinx.coroutines.flow.Flow

interface ManagementRepository {
    suspend fun getTableDetail(tableNumber: String, venueId: String)
    suspend fun getTableBill(tableBillId: String)
    fun getCachedTable() : TableDetail?
    fun setTableCache(table: TableDetail)
    fun connectToTableEvents(venueId: String, tableId: String)
    fun listenTableEvents(): Flow<PaymentUpdate>
    fun stopListeningTableEvents()
}