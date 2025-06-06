package com.avoqado.pos.features.management.data

import timber.log.Timber
import com.avoqado.pos.core.data.network.AvoqadoService
import com.avoqado.pos.core.data.network.AppConfig
import com.avoqado.pos.core.data.network.models.CreateBillRequest
import com.avoqado.pos.core.data.network.SocketIOManager
import com.avoqado.pos.core.data.network.models.NetworkVenue
import com.avoqado.pos.core.domain.models.AvoqadoError
import com.avoqado.pos.core.domain.models.PaymentUpdate
import com.avoqado.pos.core.domain.models.SplitType
import com.avoqado.pos.features.management.data.cache.ManagementCacheStorage
import com.avoqado.pos.features.management.data.mapper.toCache
import com.avoqado.pos.features.management.data.mapper.toDomain
import com.avoqado.pos.features.management.domain.ManagementRepository
import com.avoqado.pos.features.management.domain.models.BillPayment
import com.avoqado.pos.features.management.domain.models.PaymentOverview
import com.avoqado.pos.features.management.domain.models.Product
import com.avoqado.pos.features.management.domain.models.TableBillDetail
import com.avoqado.pos.features.management.domain.models.TableDetail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException

class ManagementRepositoryImpl(
    private val managementCacheStorage: ManagementCacheStorage,
    private val avoqadoService: AvoqadoService,
) : ManagementRepository {
    override suspend fun getTableDetail(
        tableNumber: String,
        venueId: String,
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun getTableBill(tableBillId: String) {
        TODO("Not yet implemented")
    }

    override fun getCachedTable(): TableDetail? = managementCacheStorage.getTable()?.toDomain()

    override fun setTableCache(table: TableDetail) {
        managementCacheStorage.setTable(table.toCache())
    }

    override fun connectToTableEvents(
        venueId: String,
        tableId: String,
    ) {
        SocketIOManager.connect(getSocketServerUrl())
        SocketIOManager.subscribeToTable(venueId, tableId)
    }

    override fun listenTableEvents(): Flow<PaymentUpdate> =
        SocketIOManager.messageFlow.map { message ->
            Timber.d("Received socket message: $message")

            // Safely parse amount
            val amount =
                try {
                    message.amount?.toDoubleOrNull() ?: 0.0
                } catch (e: Exception) {
                    Timber.e("Error parsing amount: ${message.amount}", e)
                    0.0
                }

            // Safely parse splitType with special handling for "NONE"
            val splitType =
                try {
                    if (!message.splitType.isNullOrEmpty()) {
                        if (message.splitType == "NONE") {
                            // Special handling for "NONE" which isn't in our enum
                            SplitType.EQUALPARTS // Map "NONE" to a reasonable default
                        } else {
                            SplitType.valueOf(message.splitType)
                        }
                    } else {
                        SplitType.EQUALPARTS // Default value for null
                    }
                } catch (e: Exception) {
                    Timber.e("Error parsing splitType: ${message.splitType}", e)
                    SplitType.EQUALPARTS // Fallback value
                }

            PaymentUpdate(
                amount = amount,
                splitType = splitType,
                venueId = message.venueId ?: "",
                tableNumber = message.tableNumber ?: 0,
                method = message.method ?: "",
                status = message.status, // This is correct for detecting DELETED events
                billId = message.billId,
            )
        }

    override fun listenVenueEvents(): Flow<PaymentUpdate> =
        SocketIOManager.venueMessageFlow.map { message ->
            Timber.d("Received venue message: $message")

            // Safely parse amount
            val amount =
                try {
                    message.amount?.toDoubleOrNull() ?: 0.0
                } catch (e: Exception) {
                    Timber.e("Error parsing venue amount: ${message.amount}", e)
                    0.0
                }

            // Safely parse splitType with special handling for "NONE"
            val splitType =
                try {
                    if (!message.splitType.isNullOrEmpty()) {
                        if (message.splitType == "NONE") {
                            // Special handling for "NONE" which isn't in our enum
                            SplitType.EQUALPARTS // Map "NONE" to a reasonable default
                        } else {
                            SplitType.valueOf(message.splitType)
                        }
                    } else {
                        SplitType.EQUALPARTS // Default value for null
                    }
                } catch (e: Exception) {
                    Timber.e("Error parsing venue splitType: ${message.splitType}", e)
                    SplitType.EQUALPARTS // Fallback value
                }

            // Convert tableNumber from string to int if needed
            val tableNumber =
                try {
                    message.tableNumber ?: // Remove the tableName references since they don't exist
                        0 // Default to 0 if tableNumber is null
                } catch (e: Exception) {
                    Timber.e("Error parsing tableNumber", e)
                    0
                }
            PaymentUpdate(
                amount = amount,
                splitType = splitType,
                venueId = message.venueId ?: "",
                tableNumber = tableNumber,
                method = message.method ?: "",
                status = message.status,
                billId = message.billId,
            )
        }

    override fun stopListeningTableEvents() {
        SocketIOManager.unsubscribe()
    }

    override suspend fun getVenue(venueId: String): NetworkVenue =
        try {
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

    override suspend fun getActiveBills(venueId: String): List<Pair<String, String>> =
        try {
            avoqadoService.getActiveBills(venueId).let {
                it
                    .filter { bill -> bill.status == "OPEN" }
                    .map { bill ->
                        Pair(bill.id ?: "", bill.billName ?: "")
                    }
            }
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

    override suspend fun getDetailedBill(
        venueId: String,
        billId: String,
    ): TableBillDetail =
        try {
            avoqadoService
                .getBillDetail(
                    venueId = venueId,
                    billId = billId,
                ).let { detailedBill ->
                    val totalPaid =
                        detailedBill.payments?.sumOf { payment ->
                            payment?.amount?.toInt() ?: 0
                        } ?: 0
                    val total = detailedBill.total?.toInt() ?: 0
                    TableBillDetail(
                        id = detailedBill.id ?: "",
                        products =
                            detailedBill.products?.map {
                                Product(
                                    id = it.id ?: "",
                                    name = it.name ?: "",
                                    quantity = it.quantity?.toDoubleOrNull() ?: 0.0,
                                    price = (it.price?.toDoubleOrNull() ?: 0.0) / 100,
                                )
                            } ?: emptyList(),
                        totalPending = (total - totalPaid) / 100.0,
                        name = detailedBill.tableName ?: "",
                        totalAmount =
                            detailedBill.total?.let {
                                total / 100.0
                            } ?: 0.0,
                        waiterName = if (detailedBill.waiterName?.toIntOrNull() != null) "Mesero" else detailedBill.waiterName ?: "",
                        paymentOverview =
                            detailedBill.payments?.takeIf { it.isNotEmpty() }?.let {
                                val byPerson = it.filter { payment -> payment?.splitType == SplitType.EQUALPARTS.value }
                                PaymentOverview(
                                    paidProducts =
                                        it
                                            .filter { payment -> payment?.splitType == SplitType.PERPRODUCT.value }
                                            .mapNotNull { payment ->
                                                payment?.products?.map { product -> product.id }
                                            }.flatten(),
                                    equalPartySize = byPerson.firstOrNull()?.equalPartsPartySize?.toInt() ?: 0,
                                    equalPartyPaidSize =
                                        byPerson.sumOf { payment ->
                                            payment?.equalPartsPayedFor?.toInt() ?: 0
                                        },
                                )
                            },
                        paymentsDone =
                            detailedBill.payments?.map { payment ->
                                BillPayment(
                                    amount = (payment?.amount?.toIntOrNull() ?: 0) / 100.0,
                                    products = payment?.products?.map { product -> product.id } ?: emptyList(),
                                    splitType = payment?.splitType,
                                    equalPartsPayedFor = payment?.equalPartsPayedFor,
                                    equalPartsPartySize = payment?.equalPartsPartySize,
                                )
                            } ?: emptyList(),
                    )
                }
        } catch (e: Exception) {
            if (e is HttpException) {
                if (e.code() == 401) {
                    throw AvoqadoError.Unauthorized
                } else {
                    throw AvoqadoError.BasicError(message = e.message(), code = e.code())
                }
            } else {
                throw AvoqadoError.BasicError(message = "Algo salio mal...")
            }
        }

    private fun getSocketServerUrl(): String {
        // Use the centralized ServerConfig for the socket URL
        return AppConfig.getSocketUrl()
    }
    
    /**
     * Creates a new bill with the specified name
     * 
     * @param venueId The ID of the venue where to create the bill
     * @param billName The name to assign to the new bill
     * @return true if the bill was created successfully, false otherwise
     */
    override suspend fun createNewBill(venueId: String, billName: String): Boolean {
        return try {
            Timber.d("Creating new bill: $billName for venue: $venueId")
            Timber.d("API URL: tpv/venues/$venueId/bills")
            
            // Get the current waiter info from session
            val waiterId = "" // If available from session manager
            val waiterName = "" // If available from session manager
            
            // Create a structured request body
            val requestBody = CreateBillRequest(
                tableName = billName,
                venueId = venueId,
                status = "OPEN",
                waiterName = waiterName,
                waiterId = waiterId
                // products and total will use default values from the class
            )
            
            Timber.d("Request body: $requestBody")
            
            // Make API request to create a new bill
            val response = avoqadoService.createBill(
                venueId = venueId,
                requestBody = requestBody
            )
            
            // Log full response details
            Timber.d("Response code: ${response.code()}")
            Timber.d("Response message: ${response.message()}")
            Timber.d("Response headers: ${response.headers()}")
            
            // Check if the response is successful
            val success = response.isSuccessful && response.body() != null
            
            if (success) {
                Timber.d("Successfully created new bill: ${response.body()?.id}")
            } else {
                val errorBody = response.errorBody()?.string() ?: "No error body"
                Timber.e("Failed to create bill. Code: ${response.code()}, Error: $errorBody")
            }
            
            success
        } catch (e: Exception) {
            Timber.e("Error creating new bill", e)
            e.printStackTrace() // Print the full stack trace
            false
        }
    }
}
