package com.avoqadoapp.screens.splash

import com.menta.android.core.model.LoginResponse
import com.menta.android.keys.admin.core.response.keys.MasterKeyResponseV2

sealed class SplashAction {
    data class OnExternalToken(val externalToken: LoginResponse): SplashAction()
    data class OnMasterToken(val externalToken: MasterKeyResponseV2): SplashAction()
}