package com.avoqado.pos.features.authorization.domain

import com.avoqado.pos.features.authorization.domain.models.User

interface AuthorizationRepository {
    suspend fun login(
        venueId: String,
        passcode: String,
    ): User
}
