package com.avoqado.pos.features.authorization.data

import com.avoqado.pos.core.data.local.SessionManager
import com.avoqado.pos.core.data.network.AvoqadoService
import com.avoqado.pos.core.data.network.models.PasscodeBody
import com.avoqado.pos.core.domain.models.AvoqadoError
import com.avoqado.pos.features.authorization.domain.AuthorizationRepository
import com.avoqado.pos.features.authorization.domain.models.User
import timber.log.Timber

class AuthorizationRepositoryImpl(
    private val sessionManager: SessionManager,
    private val avoqadoService: AvoqadoService,
) : AuthorizationRepository {
    override suspend fun login(
        venueId: String,
        passcode: String,
    ): User {
        try {
            val response =
                avoqadoService.loginPasscode(
                    venueId = venueId,
                    passcodeBody = PasscodeBody(passcode),
                )
            val user =
                User(
                    id = response.idmesero,
                    venueId = response.venueId,
                    name = response.nombre
                )
            sessionManager.saveAvoqadoSession(user)
            return user
        } catch (e: Exception) {
            Timber.e(e)
            throw AvoqadoError.BasicError(
                message = "Credenciales erroneas",
            )
        }
    }
}
