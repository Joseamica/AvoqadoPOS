package com.avoqado.pos.core.data.repository

import android.util.Log
import com.avoqado.pos.core.data.local.SessionManager
import com.avoqado.pos.core.data.network.AvoqadoService
import com.avoqado.pos.core.data.network.MentaService
import com.avoqado.pos.core.data.network.models.ShiftBody
import com.avoqado.pos.core.domain.models.AvoqadoError
import com.avoqado.pos.core.domain.models.Shift
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
                    createdAt = it.createdAt
                )
            }.also {
                sessionManager.setShift(it)
            }
        } catch (e: Exception) {
            Log.e("TerminalRepository", e.message ?:"", e)
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
                    createdAt = it.createdAt
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
                    createdAt = it.createdAt
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
}