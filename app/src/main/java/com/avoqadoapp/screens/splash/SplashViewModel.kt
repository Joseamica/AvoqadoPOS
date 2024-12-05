package com.avoqadoapp.screens.splash

import android.content.Context
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.avoqadoapp.ACQUIRER_NAME
import com.avoqadoapp.COUNTRY_CODE
import com.avoqadoapp.core.base.BaseViewModel
import com.avoqadoapp.core.navigation.NavigationDispatcher
import com.avoqadoapp.data.AppRestClientConfigure
import com.avoqadoapp.merchantApiKey
import com.avoqadoapp.merchantId
import com.avoqadoapp.router.destinations.MainDests
import com.menta.android.common_cross.util.StatusType
import com.menta.android.core.viewmodel.ExternalTokenData
import com.menta.android.core.viewmodel.MasterKeyData
import com.menta.android.restclient.core.RestClientConfiguration.configure
import com.menta.android.restclient.core.Storage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class SplashViewModel @Inject constructor(
    private val navigationDispatcher: NavigationDispatcher,
    private val storage: Storage,
    val externalTokenData: ExternalTokenData,
    val masterKeyData: MasterKeyData
) : BaseViewModel<SplashViewState, SplashAction>(SplashViewState()) {

    init {
        viewModelScope.launch {
            val tokenId = storage.getIdToken()
            if (tokenId.isNotEmpty()){
                delay(500L)
                navigationDispatcher.navigateTo(MainDests.Home)
            } else {
                configureTerminal()
            }
        }
    }

    override suspend fun handleActions(action: SplashAction) {
        when(action){
            is SplashAction.OnExternalToken -> {
                if (action.externalToken.status.statusType != StatusType.ERROR) {
                    Timber.d("Obtecion de token SUCCESS")
                    storage.putIdToken(action.externalToken.idToken)
                    storage.putTokenType(action.externalToken.tokenType)

                    masterKeyData.loadMasterKey(
                        merchantId = merchantId, //TODO el cliente debe conocer su merchantId, es diferente por cada comercio que tenga
                        acquirerId = ACQUIRER_NAME,
                        countryCode = COUNTRY_CODE
                    )
                } else {
                    Timber.d("Obtecion de token ERROR - ${action.externalToken.status.message}")
                }
            }
            is SplashAction.OnMasterToken -> {
                Timber.d("Inyección de llaves SUCCESS")
                //Navigate to Home
                navigationDispatcher.navigateTo(MainDests.Home)
            }
        }
    }

    private fun configureTerminal(){
        externalTokenData.getExternalToken(merchantApiKey)
    }
}