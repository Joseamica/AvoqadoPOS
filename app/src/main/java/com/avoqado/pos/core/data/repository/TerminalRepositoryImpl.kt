package com.avoqado.pos.core.data.repository


import com.avoqado.pos.core.data.local.SessionManager
import com.avoqado.pos.core.data.network.AvoqadoService
import com.avoqado.pos.core.data.network.MentaService
import com.avoqado.pos.core.data.network.SocketIOManager
import com.avoqado.pos.core.data.network.models.ShiftBody
import com.avoqado.pos.core.domain.models.AvoqadoError
import com.avoqado.pos.core.domain.models.PaymentShift
import com.avoqado.pos.core.domain.models.Shift
import com.avoqado.pos.core.domain.models.ShiftParams
import com.avoqado.pos.core.domain.models.ShiftSummary
import com.avoqado.pos.core.domain.models.TerminalInfo
import com.avoqado.pos.core.domain.repositories.TerminalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import timber.log.Timber
import java.time.Instant

class TerminalRepositoryImpl(
    private val sessionManager: SessionManager,
    private val mentaService: MentaService,
    private val avoqadoService: AvoqadoService,
) : TerminalRepository {
    override suspend fun getTerminalId(serialCode: String): TerminalInfo {
        val terminal = sessionManager.getTerminalInfo()
        if (terminal != null) {
            return TerminalInfo(
                id = terminal.id,
                serialCode = terminal.serialCode,
            )
        } else {
            //TODO: Cambiar a MetaContent para obtener datos del terminal
            return try {
                val terminals =
                    mentaService.getTerminals("")
                val currentTerminal =
                    terminals.embedded.terminals?.firstOrNull { terminal -> terminal.serialCode == serialCode }
                currentTerminal?.let {
                    sessionManager.saveTerminalInfo(it)
                    TerminalInfo(
                        id = it.id,
                        serialCode = it.serialCode,
                    )
                } ?: run {
                    throw AvoqadoError.BasicError(message = "No se encontro informacion de terminal")
                }
            } catch (e: Exception) {
                if (e is HttpException) {
                    if (e.code() == 401) {
                        throw AvoqadoError.Unauthorized
                    } else {
                        throw AvoqadoError.BasicError(message = "Algo salio mal...")
                    }
                } else {
                    throw AvoqadoError.BasicError(message = "Algo salio mal...")
                }
            }
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
                        id = it.id,
                        turnId = it.turnId,
                        insideTurnId = it.insideTurnId,
                        origin = it.origin,
                        startTime = it.startTime,
                        endTime = it.endTime,
                        fund = it.fund,
                        cash = it.cash,
                        card = it.card,
                        credit = it.credit,
                        cashier = it.cashier,
                        venueId = it.venueId,
                        updatedAt = it.updatedAt,
                        createdAt = it.createdAt,
                        avgTipPercentage = (it.avgTipPercentage ?: 0.0).toInt(),
                        tipsSum = it.tipsSum ?: 0,
                        tipsCount = it.tipsCount ?: 0,
                        paymentSum = it.paymentSum ?: 0,
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
                        id = it.id,
                        turnId = it.turnId,
                        insideTurnId = it.insideTurnId,
                        origin = it.origin,
                        startTime = it.startTime,
                        endTime = it.endTime,
                        fund = it.fund,
                        cash = it.cash,
                        card = it.card,
                        credit = it.credit,
                        cashier = it.cashier,
                        venueId = it.venueId,
                        updatedAt = it.updatedAt,
                        createdAt = it.createdAt,
                        avgTipPercentage = (it.avgTipPercentage ?: 0.0).toInt(),
                        tipsSum = it.tipsSum ?: 0,
                        tipsCount = it.tipsCount ?: 0,
                        paymentSum = it.paymentSum ?: 0,
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
                            turnId = shift?.turnId,
                            endTime = Instant.now().toString(),
                            origin = "AVOQADO_TPV",
                        ),
                ).let {
                    Shift(
                        id = it.id,
                        turnId = it.turnId,
                        insideTurnId = it.insideTurnId,
                        origin = it.origin,
                        startTime = it.startTime,
                        endTime = it.endTime,
                        fund = it.fund,
                        cash = it.cash,
                        card = it.card,
                        credit = it.credit,
                        cashier = it.cashier,
                        venueId = it.venueId,
                        updatedAt = it.updatedAt,
                        createdAt = it.createdAt,
                        avgTipPercentage = (it.avgTipPercentage ?: 0.0).toInt(),
                        tipsSum = it.tipsSum ?: 0,
                        tipsCount = it.tipsCount ?: 0,
                        paymentSum = it.paymentSum ?: 0,
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
                .getShiftSummary(
                    venueId = params.venueId,
                    pageSize = params.pageSize,
                    pageNumber = params.page,
                    startTime = params.startTime,
                    endTime = params.endTime,
                    waiterId = params.waiterIds,
                ).let { data ->
                    data.data.map {
                        Shift(
                            id = it.id,
                            turnId = it.turnId,
                            insideTurnId = it.insideTurnId,
                            origin = it.origin,
                            startTime = it.startTime,
                            endTime = it.endTime,
                            fund = it.fund,
                            cash = it.cash,
                            card = it.card,
                            credit = it.credit,
                            cashier = it.cashier,
                            venueId = it.venueId,
                            updatedAt = it.updatedAt,
                            createdAt = it.createdAt,
                            avgTipPercentage = (it.avgTipPercentage ?: 0.0).toInt(),
                            tipsSum = it.tipsSum ?: 0,
                            tipsCount = it.tipsCount ?: 0,
                            paymentSum = it.paymentSum ?: 0,
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
                        totalSales = it.data?.summary?.totalSales ?: 0,
                        totalTips = it.data?.summary?.totalTips ?: 0,
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

    override suspend fun getShiftPaymentsSummary(params: ShiftParams): List<PaymentShift> =
        try {
            avoqadoService
                .getPaymentsSummary(
                    venueId = params.venueId,
                    pageSize = params.pageSize,
                    pageNumber = params.page,
                    startTime = params.startTime,
                    endTime = params.endTime,
                    waiterId = params.waiterIds,
                ).let { data ->
                    data.data?.map {
                        PaymentShift(
                            id = it?.id ?: "",
                            waiterName = it?.waiter?.nombre ?: "",
                            totalSales = it?.amount?.toInt() ?: 0,
                            totalTip =
                                it
                                    ?.tips
                                    ?.firstOrNull()
                                    ?.amount
                                    ?.toInt() ?: 0,
                            paymentId = it?.mentaTicketId ?: "",
                            date =
                                it?.createdAt?.let {
                                    Instant.parse(it)
                                } ?: Instant.now(),
                            createdAt =
                                it?.createdAt?.let {
                                    Instant.parse(it)
                                } ?: Instant.now(),
                        )
                    } ?: emptyList()
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
        // Verificamos si el socket está conectado
        if (!SocketIOManager.isConnected()) {
            SocketIOManager.connect(SocketIOManager.getServerUrl())
        }

        // Nos unimos a la sala del venue para escuchar eventos
        SocketIOManager.joinMobileRoom(venueId)
    }

    override fun disconnectFromShiftEvents() {
        // Si tenemos un currentVenueRoomId en SocketIOManager, nos desconectamos
        SocketIOManager.disconnect()
    }

    override fun listenForShiftEvents(): Flow<Shift> =
        SocketIOManager.shiftMessageFlow.map { message ->
            Timber.d("Received shift update: $message")

            // Convertimos el mensaje a un objeto Shift
            val shift =
    Shift(
        id = message.id,
        turnId = message.turnId,
        insideTurnId = message.insideTurnId,
        origin = message.origin,
        startTime = message.startTime,
        endTime = message.endTime,
        fund = message.fund?.toString(),
        cash = message.cash?.toString(),
        card = message.card?.toString(),
        credit = message.credit?.toString(),
        cashier = message.cashier,
        venueId = message.venueId,
        updatedAt = message.updatedAt?.toString(),
        createdAt = message.createdAt?.toString(),
        avgTipPercentage = 0, // Default value
        tipsSum = 0,          // Default value
        tipsCount = 0,        // Default value
        paymentSum = 0        // Default value
    )

            // Guardamos el turno en SessionManager
            sessionManager.setShift(shift)

            shift
        }
        
    override suspend fun getPaymentById(paymentId: String): PaymentShift? {
        try {
            // Get the current venue ID from session
            val venueId = sessionManager.getVenueId() ?: return null
            
            // Set parameters to get all payments for the current venue
            val params = ShiftParams(
                venueId = venueId,
                pageSize = 100, // Set a reasonable page size to ensure we retrieve enough payments
                page = 1,
                startTime = null, // No time restrictions
                endTime = null
            )
            
            // Get all payments and find the one that matches the paymentId
            val payments = getShiftPaymentsSummary(params)
            return payments.find { it.paymentId == paymentId }
        } catch (e: Exception) {
            Timber.e(e, "Error finding payment by ID: $paymentId")
            return null
        }
    }
}
