package com.avoqado.pos.features.management.domain

import com.avoqado.pos.features.management.domain.models.TableDetail
import kotlinx.coroutines.flow.Flow

interface ManagementRepository {
    fun getTableBill()
    fun getCachedTable() : TableDetail?
    fun setTableCache(table: TableDetail)
    fun connectToTableEvents(venueId: String, tableId: String)
    fun listenTableEvents(): Flow<String>
    fun stopListeningTableEvents()
}