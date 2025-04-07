package com.avoqado.pos.features.management.data

import android.util.Log
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
    private val avoqadoService: AvoqadoService
) : ManagementRepository {

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

    override suspend fun getActiveBills(venueId: String): List<Pair<String, String>> {
        return try {
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
    }

    override suspend fun getDetailedBill(venueId: String, billId: String): TableBillDetail {
        return try {
            avoqadoService.getBillDetail(
                venueId = venueId,
                billId = billId
            ).let { detailedBill ->
                val totalPaid = detailedBill.payments?.sumOf { payment->
                    payment?.amount?.toInt() ?: 0
                } ?: 0
                val total = detailedBill.total?.toInt() ?: 0
                TableBillDetail(
                    id = detailedBill.id ?: "",
                    products = detailedBill.products?.map {
                        Product(
                            id = it.id ?: "",
                            name = it.name ?: "",
                            quantity = it.quantity?.toDoubleOrNull() ?: 0.0,
                            price = (it.price?.toDoubleOrNull() ?: 0.0) / 100
                        )
                    } ?: emptyList(),
                    totalPending = (total - totalPaid) / 100.0,
                    name = detailedBill.tableName ?: "",
                    totalAmount = detailedBill.total?.let {
                        total / 100.0
                    } ?: 0.0,
                    waiterName = detailedBill.waiterName ?: "",
                    paymentOverview = detailedBill.payments?.takeIf { it.isNotEmpty() }?.let {
                        val byPerson = it.filter { payment -> payment?.splitType == SplitType.EQUALPARTS.value }
                        PaymentOverview(
                            paidProducts = it.filter { payment -> payment?.splitType == SplitType.PERPRODUCT.value }
                                .mapNotNull { payment ->
                                    payment?.products?.map { product ->  product.id }
                                }.flatten(),
                            equalPartySize = byPerson.firstOrNull()?.equalPartsPartySize?.toInt() ?: 0,
                            equalPartyPaidSize = byPerson.sumOf { payment ->
                                payment?.equalPartsPayedFor?.toInt() ?: 0
                            }
                        )
                    },
                    paymentsDone = detailedBill.payments?.map { payment ->
                        BillPayment(
                            amount = (payment?.amount?.toIntOrNull() ?: 0) / 100.0,
                            products = payment?.products?.map { product -> product.id } ?: emptyList(),
                            splitType = payment?.splitType,
                            equalPartsPayedFor = payment?.equalPartsPayedFor,
                            equalPartsPartySize = payment?.equalPartsPartySize
                        )
                    } ?: emptyList()
                )
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
    }
}