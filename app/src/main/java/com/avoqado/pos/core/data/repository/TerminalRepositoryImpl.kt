package com.avoqado.pos.core.data.repository

import android.util.Log
import com.avoqado.pos.core.data.local.SessionManager
import com.avoqado.pos.core.data.network.AvoqadoService
import com.avoqado.pos.core.data.network.MentaService
import com.avoqado.pos.core.data.network.models.ShiftBody
import com.avoqado.pos.core.domain.models.AvoqadoError
import com.avoqado.pos.core.domain.models.PaymentShift
import com.avoqado.pos.core.domain.models.Shift
import com.avoqado.pos.core.domain.models.ShiftParams
import com.avoqado.pos.core.domain.models.ShiftSummary
import com.avoqado.pos.core.domain.models.TerminalInfo
import com.avoqado.pos.core.domain.repositories.TerminalRepository
import com.menta.android.restclient.core.Storage
import retrofit2.HttpException
import timber.log.Timber
import java.time.Instant

class TerminalRepositoryImpl(
    private val sessionManager: SessionManager,
    private val mentaService: MentaService,
    private val storage: Storage,
    private val avoqadoService: AvoqadoService
) : TerminalRepository {
    override suspend fun getTerminalId(serialCode: String): TerminalInfo {
        val terminal = sessionManager.getTerminalInfo()
        if (terminal != null) {
            return TerminalInfo(
                id = terminal.id,
                serialCode = terminal.serialCode
            )
        } else {
            return try {
                val terminals =
                    mentaService.getTerminals("${storage.getTokenType()} ${storage.getIdToken()}")
                val currentTerminal =
                    terminals.embedded.terminals?.firstOrNull { terminal -> terminal.serialCode == serialCode }
                currentTerminal?.let {
                    sessionManager.saveTerminalInfo(it)
                    TerminalInfo(
                        id = it.id,
                        serialCode = it.serialCode
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

    override suspend fun getTerminalShift(venueId: String, posName: String): Shift {
        return try {
            avoqadoService.getRestaurantShift(venueId = venueId, posName = posName).let {
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
                    avgTipPercentage = it.avgTipPercentage ?: 0,
                    tipsSum = it.tipsSum ?: 0,
                    tipsCount = it.tipsCount ?: 0,
                    paymentSum = it.paymentSum ?: 0
                )
            }.also {
                sessionManager.setShift(it)
            }
        } catch (e: Exception) {
            Log.e("TerminalRepository", e.message ?: "", e)
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

    override suspend fun startTerminalShift(venueId: String, posName: String): Shift {
        return try {
            avoqadoService.registerRestaurantShift(
                venueId = venueId, body = ShiftBody(
                    posName = posName,
                    turnId = null,
                    endTime = null,
                    origin = null,
                )
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
                    avgTipPercentage = it.avgTipPercentage ?: 0,
                    tipsSum = it.tipsSum ?: 0,
                    tipsCount = it.tipsCount ?: 0,
                    paymentSum = it.paymentSum ?: 0
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
    }

    override suspend fun closeTerminalShift(venueId: String, posName: String): Shift {
        val shift = sessionManager.getShift()
        return try {
            avoqadoService.updateRestaurantShift(
                venueId = venueId, body = ShiftBody(
                    posName = posName,
                    turnId = shift?.turnId,
                    endTime = Instant.now().toString(),
                    origin = "AVOQADO_POS",
                )
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
                    avgTipPercentage = it.avgTipPercentage ?: 0,
                    tipsSum = it.tipsSum ?: 0,
                    tipsCount = it.tipsCount ?: 0,
                    paymentSum = it.paymentSum ?: 0
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

    override suspend fun getShiftSummary(params: ShiftParams): List<Shift> {
        return try {
            avoqadoService.getShiftSummary(
                venueId = params.venueId,
                pageSize = params.pageSize,
                pageNumber = params.page,
                startTime = params.startTime,
                endTime = params.endTime,
                waiterId = params.waiterIds
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
                        avgTipPercentage = it.avgTipPercentage ?: 0,
                        tipsSum = it.tipsSum ?: 0,
                        tipsCount = it.tipsCount ?: 0,
                        paymentSum = it.paymentSum ?: 0
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
    }

    override suspend fun getSummary(params: ShiftParams): ShiftSummary {
        return try {
            avoqadoService.getSummary(
                venueId = params.venueId,
                startTime = params.startTime,
                endTime = params.endTime,
                waiterId = params.waiterIds
            ).let {
                ShiftSummary(
                    tips = it.data?.waiterTips?.map { tip ->
                        Pair(
                            tip?.name ?: "",
                            tip?.amount?.toString() ?: ""
                        )
                    } ?: emptyList(),
                    averageTipPercentage = it.data?.summary?.averageTipPercentage ?: 0.0,
                    ordersCount = it.data?.summary?.ordersCount ?: 0,
                    ratingsCount = it.data?.summary?.ratingsCount ?: 0,
                    totalSales = it.data?.summary?.totalSales ?: 0,
                    totalTips = it.data?.summary?.totalTips ?: 0
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
    }

    override suspend fun getShiftPaymentsSummary(params: ShiftParams): List<PaymentShift> {
        return try {
            avoqadoService.getPaymentsSummary(
                venueId = params.venueId,
                pageSize = params.pageSize,
                pageNumber = params.page,
                startTime = params.startTime,
                endTime = params.endTime,
                waiterId = params.waiterIds
            ).let { data ->
                data.data?.map {
                    PaymentShift(
                        id = it?.id ?: "",
                        waiterName = it?.waiter?.nombre ?: "",
                        totalSales = it?.amount?.toInt() ?: 0,
                        totalTip = it?.tips?.firstOrNull()?.amount?.toInt() ?: 0,
                        paymentId = it?.mentaTicketId ?: "",
                        date = it?.createdAt?.let {
                            Instant.parse(it)
                        }?: Instant.now()
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
    }

}
