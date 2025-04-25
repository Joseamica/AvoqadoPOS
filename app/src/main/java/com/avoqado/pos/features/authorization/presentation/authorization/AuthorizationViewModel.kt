package com.avoqado.pos.features.authorization.presentation.authorization

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avoqado.pos.AppfinRestClientConfigure
import com.avoqado.pos.core.data.local.SessionManager
import com.avoqado.pos.core.presentation.navigation.NavigationDispatcher
import com.avoqado.pos.features.authorization.presentation.splash.SplashViewModel.Companion.GET_MASTER_KEY
import com.avoqado.pos.features.authorization.presentation.splash.SplashViewModel.Companion.START_CONFIG
import com.avoqado.pos.views.InitActivity.Companion.TAG
import com.menta.android.keys.admin.core.response.keys.SecretsV2
import com.menta.android.restclient.core.RestClientConfiguration.configure
import com.menta.android.restclient.core.Storage
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class AuthorizationViewModel(
    private val navigationDispatcher: NavigationDispatcher,
    private val storage: Storage,
    private val sessionManager: SessionManager,
) : ViewModel() {
    val currentUser = sessionManager.getAvoqadoSession()
    val operationPreference = sessionManager.getOperationPreference()

    private val _isConfiguring = MutableStateFlow(false)
    val isConfiguring: StateFlow<Boolean> = _isConfiguring.asStateFlow()

    private val _events = Channel<String>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        Log.i("SplashViewModel", "Init")
        startConfiguring()
    }

    private fun startConfiguring() {
        _isConfiguring.value = true
        configure(AppfinRestClientConfigure())
        viewModelScope.launch { _events.send(START_CONFIG) }
    }

    fun storePublicKey(
        token: String,
        tokenType: String,
    ) {
        // Guardar el token
        storage.putIdToken(token)
        storage.putTokenType(tokenType)
        viewModelScope.launch { _events.send(GET_MASTER_KEY) }
    }

    fun handleMasterKey(secretsList: ArrayList<SecretsV2>?) {
        if (secretsList != null) {
            // TODO: aca se debe verificar si el usuario esta logeado en Avoqado API
            navigationDispatcher.navigateBack()
        } else {
            Log.i(TAG, "Inyección de llaves ERROR")
        }
    }
}
