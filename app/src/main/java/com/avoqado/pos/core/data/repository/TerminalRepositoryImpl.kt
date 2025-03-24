package com.avoqado.pos.core.data.repository

import com.avoqado.pos.core.data.local.SessionManager
import com.avoqado.pos.core.data.network.AvoqadoService
import com.avoqado.pos.core.data.network.MentaService
import com.avoqado.pos.core.domain.models.AvoqadoError
import com.avoqado.pos.core.domain.models.TerminalInfo
import com.avoqado.pos.core.domain.repositories.TerminalRepository
import com.menta.android.restclient.core.Storage
import retrofit2.HttpException
import timber.log.Timber

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
                serialCode= terminal.serialCode
            )
        } else {
            return try {
                val terminals = mentaService.getTerminals("${storage.getTokenType()} ${storage.getIdToken()}")
                val currentTerminal = terminals.embedded.terminals?.firstOrNull { terminal -> terminal.serialCode == serialCode }
                currentTerminal?.let {
                    sessionManager.saveTerminalInfo(it)
                    TerminalInfo(
                        id = it.id,
                        serialCode = it.serialCode
                    )
                } ?: run {
                    throw AvoqadoError.BasicError(message = "No se encontro informacion de terminal")
                }
            }
            catch (e: Exception) {
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

    override suspend fun getTerminalShift(venueId: String): String {
        return try {
//            val shift = avoqadoService.getRestaurantShift(venueId = venueId)
//            Timber.i(shift.toString())
            ""
        }
        catch (e: Exception) {
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