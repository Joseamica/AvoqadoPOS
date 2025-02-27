package com.avoqado.pos.features.authorization.domain

import com.avoqado.pos.features.authorization.domain.models.User

interface AuthorizationRepository {
    fun login(
        user: String,
        passcode: String
    ): User
}