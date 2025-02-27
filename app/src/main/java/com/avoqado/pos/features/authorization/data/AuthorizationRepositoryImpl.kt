package com.avoqado.pos.features.authorization.data

import com.avoqado.pos.core.data.local.SessionManager
import com.avoqado.pos.core.domain.models.AvoqadoError
import com.avoqado.pos.features.authorization.domain.AuthorizationRepository
import com.avoqado.pos.features.authorization.domain.models.User

class AuthorizationRepositoryImpl(
    private val sessionManager: SessionManager
) : AuthorizationRepository {
    override fun login(
        user: String,
        passcode: String
    ): User {
            if (passcode == "1234" && user == "test@avoqado.io"){
                val user = User(
                    id = "test",
                    venues = listOf(
                        Pair("madre_cafecito","8e341c9a-0298-4aa1-ba6b-be11a526560f")
                    ),
                    apiKey = "KnLfOhIkTql8OUQ2NCltlUb6qonNMcNpTsky3iaz4IfNn8tzN8Rl0Dzc16THsz8E"
                )
                sessionManager.saveAvoqadoSession(user)
                return user
            } else {
                throw AvoqadoError.BasicError(
                    message = "Credenciales erroneas"
                )
            }
    }
}