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
    private val avoqadoService: AvoqadoService
) : AuthorizationRepository {
    override suspend fun login(
        venueId: String,
        passcode: String
    ): User {
        try {
            val response = avoqadoService.loginPasscode(
                venueId = venueId,
                passcodeBody = PasscodeBody(passcode)
            )
            val user = User(
                id = response.idmesero,
                venueId = response.venueId,
                name = response.nombre,
                primaryMerchantId = "8e341c9a-0298-4aa1-ba6b-be11a526560f",
                apiKey = "4mrOrkW27ZDvmNiDZVdDatcLrQQvVPH5Www1OlT8TL767v92P3e7DP6SmdW6zRW7",
                secondaryMerchantId = "d6457a4d-1ee2-4596-ae76-17ca9465b20a", // <- Merchant
                secondaryApiKey = "6LHEsrMSosuaaDZ79HOkf1rOLip3DvndU8bGFOkrc1h31EpRMX1CpRgp0gQyq5ym" // "ACA_NUEVO_MERCHANT_API+KEY"
            )
            sessionManager.saveAvoqadoSession(user)
            return user
        } catch (e: Exception) {
            Timber.e(e)
            throw AvoqadoError.BasicError(
                message = "Credenciales erroneas"
            )
        }
    }
}