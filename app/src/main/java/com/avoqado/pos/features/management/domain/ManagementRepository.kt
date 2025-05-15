package com.avoqado.pos.features.management.domain

import com.avoqado.pos.core.data.network.models.NetworkVenue
import com.avoqado.pos.core.domain.models.PaymentUpdate
import com.avoqado.pos.features.management.domain.models.TableBillDetail
import com.avoqado.pos.features.management.domain.models.TableDetail
import kotlinx.coroutines.flow.Flow

interface ManagementRepository {
    suspend fun getTableDetail(
        tableNumber: String,
        venueId: String,
    )

    suspend fun getTableBill(tableBillId: String)

    fun getCachedTable(): TableDetail?

    fun setTableCache(table: TableDetail)

    // Connect to WebSocket for table-specific events
    fun connectToTableEvents(
        venueId: String,
        tableId: String,
    )

    // Listen for table-specific events
    fun listenTableEvents(): Flow<PaymentUpdate>

    // Stop listening for table events
    fun stopListeningTableEvents()

    // New function to listen for venue-wide events
    fun listenVenueEvents(): Flow<PaymentUpdate>

    suspend fun getVenue(venueId: String): NetworkVenue

    suspend fun getActiveBills(venueId: String): List<Pair<String, String>>

    suspend fun getDetailedBill(
        venueId: String,
        billId: String,
    ): TableBillDetail
    
    /**
     * Create a new bill with the given name
     * 
     * @param venueId The ID of the venue where to create the bill
     * @param billName The name to assign to the new bill
     * @return true if the bill was created successfully, false otherwise
     */
    suspend fun createNewBill(venueId: String, billName: String): Boolean
}
