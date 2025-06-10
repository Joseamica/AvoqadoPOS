package com.avoqado.pos.features.authorization.presentation.authorization

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avoqado.pos.core.data.local.SessionManager
import com.avoqado.pos.core.presentation.navigation.NavigationDispatcher
import com.avoqado.pos.features.authorization.presentation.splash.SplashViewModel.Companion.GET_MASTER_KEY
import com.avoqado.pos.features.authorization.presentation.splash.SplashViewModel.Companion.START_CONFIG
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class AuthorizationViewModel(
    private val navigationDispatcher: NavigationDispatcher,
    private val sessionManager: SessionManager,
) : ViewModel() {
    val currentUser = sessionManager.getAvoqadoSession()
    val operationPreference = sessionManager.getOperationPreference()

    private val _isConfiguring = MutableStateFlow(false)
    val isConfiguring: StateFlow<Boolean> = _isConfiguring.asStateFlow()

    private val _events = Channel<String>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        Timber.i("Init AuthorizationViewModel")
        startConfiguring()
    }

    private fun startConfiguring() {
        _isConfiguring.value = true
        viewModelScope.launch { _events.send(START_CONFIG) }
    }

    fun storePublicKey(
        token: String,
        tokenType: String,
    ) {
        // Guardar el token
        viewModelScope.launch { _events.send(GET_MASTER_KEY) }
    }

}
