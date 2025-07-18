package com.avoqado.pos.core.data.repository

import com.avoqado.pos.core.data.local.SessionManager
import com.avoqado.pos.core.data.network.AvoqadoService
import com.avoqado.pos.core.data.network.MentaService
import com.avoqado.pos.core.data.network.SocketIOManager
import com.avoqado.pos.core.data.network.models.ShiftBody
import com.avoqado.pos.core.domain.models.AvoqadoError
import com.avoqado.pos.core.domain.models.Payment
import com.avoqado.pos.core.domain.models.Order
import com.avoqado.pos.core.domain.models.ProcessedBy
import com.avoqado.pos.core.domain.models.Shift
import com.avoqado.pos.core.domain.models.ShiftParams
import com.avoqado.pos.core.domain.models.ShiftSummary
import com.avoqado.pos.core.domain.models.ShiftStatus
import com.avoqado.pos.core.domain.models.TerminalInfo
import com.avoqado.pos.core.domain.repositories.TerminalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import timber.log.Timber
import java.time.Instant

class TerminalRepositoryImpl(
    private val sessionManager: SessionManager,
    private val socketIOManager: SocketIOManager,
    private val mentaService: MentaService,
    private val avoqadoService: AvoqadoService,
) : TerminalRepository {

    override suspend fun getTerminalId(serialCode: String): TerminalInfo =
        try {
            avoqadoService.getTPV(serialCode).let {
                TerminalInfo(
                    id = serialCode, // Use serialCode as id since it.id might not exist
                    serialCode = serialCode,
                    venueId = null, // TODO: Map from actual network model
                    venue = null, // TODO: Map from actual network model
                    status = null, // TODO: Map from actual network model
                    isActive = null, // TODO: Map from actual network model
                    createdAt = null, // TODO: Map from actual network model
                    updatedAt = null, // TODO: Map from actual network model
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

    override suspend fun getTerminalShift(
        venueId: String,
        posName: String,
    ): Shift =
        try {
            avoqadoService
                .getRestaurantShift(venueId = venueId, posName = posName)
                .let {
                    Shift(
                        id = it.id ?: "",
                        venueId = it.venueId ?: venueId,
                        staffId = "", // TODO: Get actual staff ID
                        startTime = it.startTime?.let { time -> Instant.parse(time) } ?: Instant.now(),
                        endTime = it.endTime?.let { time -> Instant.parse(time) },
                        startingCash = 0.0, // TODO: Map from legacy data
                        endingCash = null,
                        cashDifference = null,
                        totalSales = 0.0, // TODO: Map from legacy data
                        totalTips = 0.0, // TODO: Map from legacy data
                        totalOrders = 0,
                        status = if (it.endTime.isNullOrBlank()) ShiftStatus.OPEN else ShiftStatus.CLOSED,
                        notes = null,
                        originSystem = it.origin ?: "AVOQADO",
                        externalId = null,
                        createdAt = it.createdAt?.let { time -> Instant.parse(time) } ?: Instant.now(),
                        updatedAt = it.updatedAt?.let { time -> Instant.parse(time) } ?: Instant.now(),
                        staff = null, // TODO: Get actual staff info
                        orders = null,
                        payments = null
                    )
                }.also {
                    sessionManager.setShift(it)
                }
        } catch (e: Exception) {
            Timber.e(e, e.message ?: "")
            if (e is HttpException) {
                if (e.code() == 401) {
                    throw AvoqadoError.Unauthorized
                } else {
                    if (e.code() == 404) {
                        sessionManager.clearShift()
                    }
                    throw AvoqadoError.BasicError(message = e.message())
                }
            } else {
                throw AvoqadoError.BasicError(message = "Algo salio mal...")
            }
        }

    override suspend fun startTerminalShift(
        venueId: String,
        posName: String,
    ): Shift =
        try {
            avoqadoService
                .registerRestaurantShift(
                    venueId = venueId,
                    body =
                        ShiftBody(
                            posName = posName,
                            turnId = null,
                            endTime = null,
                            origin = null,
                        ),
                ).let {
                    Shift(
                        id = it.id ?: "",
                        venueId = it.venueId ?: venueId,
                        staffId = "", // TODO: Get actual staff ID
                        startTime = it.startTime?.let { time -> Instant.parse(time) } ?: Instant.now(),
                        endTime = it.endTime?.let { time -> Instant.parse(time) },
                        startingCash = 0.0, // TODO: Map from legacy data
                        endingCash = null,
                        cashDifference = null,
                        totalSales = 0.0, // TODO: Map from legacy data
                        totalTips = 0.0, // TODO: Map from legacy data
                        totalOrders = 0,
                        status = if (it.endTime.isNullOrBlank()) ShiftStatus.OPEN else ShiftStatus.CLOSED,
                        notes = null,
                        originSystem = it.origin ?: "AVOQADO",
                        externalId = null,
                        createdAt = it.createdAt?.let { time -> Instant.parse(time) } ?: Instant.now(),
                        updatedAt = it.updatedAt?.let { time -> Instant.parse(time) } ?: Instant.now(),
                        staff = null, // TODO: Get actual staff info
                        orders = null,
                        payments = null
                    )
                }.also {
                    sessionManager.setShift(it)
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

    override suspend fun closeTerminalShift(
        venueId: String,
        posName: String,
    ): Shift {
        val shift = sessionManager.getShift()
        return try {
            avoqadoService
                .updateRestaurantShift(
                    venueId = venueId,
                    body =
                        ShiftBody(
                            posName = posName,
                            turnId = null, // TODO: Get from shift if needed
                            endTime = Instant.now().toString(),
                            origin = "AVOQADO_TPV",
                        ),
                ).let {
                    Shift(
                        id = it.id ?: "",
                        venueId = it.venueId ?: venueId,
                        staffId = "", // TODO: Get actual staff ID
                        startTime = it.startTime?.let { time -> Instant.parse(time) } ?: Instant.now(),
                        endTime = it.endTime?.let { time -> Instant.parse(time) },
                        startingCash = 0.0, // TODO: Map from legacy data
                        endingCash = null,
                        cashDifference = null,
                        totalSales = 0.0, // TODO: Map from legacy data
                        totalTips = 0.0, // TODO: Map from legacy data
                        totalOrders = 0,
                        status = if (it.endTime.isNullOrBlank()) ShiftStatus.OPEN else ShiftStatus.CLOSED,
                        notes = null,
                        originSystem = it.origin ?: "AVOQADO",
                        externalId = null,
                        createdAt = it.createdAt?.let { time -> Instant.parse(time) } ?: Instant.now(),
                        updatedAt = it.updatedAt?.let { time -> Instant.parse(time) } ?: Instant.now(),
                        staff = null, // TODO: Get actual staff info
                        orders = null,
                        payments = null
                    )
                }.also {
                    sessionManager.clearShift()
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

    override suspend fun getShiftSummary(params: ShiftParams): List<Shift> =
        try {
            avoqadoService
                .getShifts(
                    venueId = params.venueId,
                    pageSize = params.pageSize,
                    pageNumber = params.page,
                    startTime = params.startTime,
                    endTime = params.endTime,
                    waiterId = params.waiterIds,
                ).let { response ->
                    response.data.map { networkShift ->
                        Shift(
                            id = networkShift.id,
                            venueId = networkShift.venueId,
                            staffId = networkShift.staffId,
                            startTime = Instant.parse(networkShift.startTime),
                            endTime = networkShift.endTime?.let { Instant.parse(it) },
                            startingCash = networkShift.startingCash.toDoubleOrNull() ?: 0.0,
                            endingCash = networkShift.endingCash?.toDoubleOrNull(),
                            cashDifference = networkShift.cashDifference?.toDoubleOrNull(),
                            totalSales = networkShift.totalSales.toDoubleOrNull() ?: 0.0,
                            totalTips = networkShift.totalTips.toDoubleOrNull() ?: 0.0,
                            totalOrders = networkShift.totalOrders,
                            status = when (networkShift.status) {
                                "OPEN" -> ShiftStatus.OPEN
                                "CLOSED" -> ShiftStatus.CLOSED
                                else -> ShiftStatus.PENDING
                            },
                            notes = networkShift.notes,
                            originSystem = networkShift.originSystem,
                            externalId = networkShift.externalId,
                            createdAt = Instant.parse(networkShift.createdAt),
                            updatedAt = Instant.parse(networkShift.updatedAt),
                            staff = networkShift.staff?.let {
                                com.avoqado.pos.core.domain.models.ShiftStaff(
                                    id = it.id,
                                    firstName = it.firstName,
                                    lastName = it.lastName
                                )
                            },
                            orders = networkShift.orders?.map { order ->
                                com.avoqado.pos.core.domain.models.ShiftOrder(
                                    id = order.id,
                                    orderNumber = order.orderNumber,
                                    total = order.total.toDoubleOrNull() ?: 0.0,
                                    status = order.status
                                )
                            },
                            payments = networkShift.payments?.map { payment ->
                                com.avoqado.pos.core.domain.models.ShiftPayment(
                                    id = payment.id,
                                    amount = payment.amount.toDoubleOrNull() ?: 0.0,
                                    tipAmount = payment.tipAmount.toDoubleOrNull() ?: 0.0,
                                    method = payment.method,
                                    status = payment.status
                                )
                            }
                        )
                    }
                }
        } catch (e: Exception) {
            Timber.e(e)
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

    override suspend fun getSummary(params: ShiftParams): ShiftSummary =
        try {
            avoqadoService
                .getSummary(
                    venueId = params.venueId,
                    startTime = params.startTime,
                    endTime = params.endTime,
                    waiterId = params.waiterIds,
                ).let {
                    ShiftSummary(
                        tips =
                            it.data?.waiterTips?.map { tip ->
                                Pair(
                                    tip?.name ?: "",
                                    tip?.amount?.toString() ?: "",
                                )
                            } ?: emptyList(),
                        averageTipPercentage = it.data?.summary?.averageTipPercentage ?: 0.0,
                        ordersCount = it.data?.summary?.ordersCount ?: 0,
                        ratingsCount = it.data?.summary?.ratingsCount ?: 0,
                        totalSales = it.data?.summary?.totalSales?.toDouble() ?: 0.0,
                        totalTips = it.data?.summary?.totalTips?.toDouble() ?: 0.0,
                    )
                }
        } catch (e: Exception) {
            Timber.e(e)
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

    override suspend fun getPayments(params: ShiftParams): List<Payment> =
        try {
            avoqadoService
                .getPayments(
                    venueId = params.venueId,
                    pageSize = params.pageSize,
                    pageNumber = params.page,
                    startTime = params.startTime,
                    endTime = params.endTime,
                    waiterId = params.waiterIds,
                    paymentId = params.paymentId,
                ).let { data ->
                    data.data.map { networkPayment ->
                        Payment(
                            id = networkPayment.id,
                            venueId = networkPayment.venueId,
                            orderId = networkPayment.orderId,
                            shiftId = networkPayment.shiftId,
                            processedById = networkPayment.processedById,
                            amount = networkPayment.amount.toDoubleOrNull() ?: 0.0,
                            tipAmount = networkPayment.tipAmount.toDoubleOrNull() ?: 0.0,
                            method = networkPayment.method,
                            status = networkPayment.status,
                            processor = networkPayment.processor,
                            processorId = networkPayment.processorId,
                            feePercentage = networkPayment.feePercentage?.toDoubleOrNull(),
                            feeAmount = networkPayment.feeAmount?.toDoubleOrNull(),
                            netAmount = networkPayment.netAmount?.toDoubleOrNull(),
                            externalId = networkPayment.externalId,
                            originSystem = networkPayment.originSystem,
                            syncStatus = networkPayment.syncStatus,
                            syncedAt = networkPayment.syncedAt?.let { date -> Instant.parse(date) },
                            createdAt = Instant.parse(networkPayment.createdAt),
                            updatedAt = Instant.parse(networkPayment.updatedAt),
                            processedBy = networkPayment.processedBy?.let {
                                ProcessedBy(it.id, it.firstName, it.lastName)
                            },
                            order = networkPayment.order?.let { o -> Order(o.id) },
                            allocations = emptyList(), // TODO: Map allocations if needed
                            tableNumber = networkPayment.order?.table?.number
                        )
                    }
                }
        } catch (e: Exception) {
            Timber.e(e)
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

    override fun connectToShiftEvents(venueId: String) {
        socketIOManager.connect(venueId)
    }

    override fun disconnectFromShiftEvents() {
        socketIOManager.disconnect()
    }

    override fun listenForShiftEvents(): Flow<Shift> = flow {
        // TODO: Implement socket listening for shift updates
        // This is a placeholder implementation
        // socketIOManager.listen("shiftUpdate").collect { shiftUpdateMessage ->
        //     emit(com.avoqado.pos.core.domain.mappers.ShiftMapper.mapToDomain(shiftUpdateMessage))
        // }
    }

    override suspend fun getPaymentById(paymentId: String): Payment? =
        try {
            sessionManager.getVenueInfo()?.let { venueInfo ->
                val params = ShiftParams(
                    venueId = venueInfo.id ?: "",
                    page = 1,
                    pageSize = 1,
                    paymentId = paymentId
                )
                getPayments(params).firstOrNull()
            }
        } catch (e: Exception) {
            Timber.e(e, "Error getting payment by ID: $paymentId")
            null
        }
}
