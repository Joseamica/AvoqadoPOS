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
                    id = "test_user",
                    venueId = "madre_cafecito",
                    primaryMerchantId = "8e341c9a-0298-4aa1-ba6b-be11a526560f",
                    apiKey = "4mrOrkW27ZDvmNiDZVdDatcLrQQvVPH5Www1OlT8TL767v92P3e7DP6SmdW6zRW7",
                    secondaryMerchantId = "d6457a4d-1ee2-4596-ae76-17ca9465b20a", // <- Merchant
                    secondaryApiKey = "6LHEsrMSosuaaDZ79HOkf1rOLip3DvndU8bGFOkrc1h31EpRMX1CpRgp0gQyq5ym" // "ACA_NUEVO_MERCHANT_API+KEY"
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