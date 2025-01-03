package com.avoqado.pos.screens.splash

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavOptions
import androidx.navigation.navOptions
import com.avoqado.pos.AppfinRestClientConfigure
import com.avoqado.pos.core.navigation.NavigationDispatcher
import com.avoqado.pos.destinations.MainDests
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

class SplashViewModel(
    private val navigationDispatcher: NavigationDispatcher,
    private val storage: Storage
) : ViewModel() {

    companion object {
        val START_CONFIG = "START_CONFIG"
        val GET_MASTER_KEY = "GET_MASTER_KEY"
    }

    private val _isConfiguring = MutableStateFlow(false)
    val isConfiguring: StateFlow<Boolean> = _isConfiguring.asStateFlow()

    private val _events = Channel<String>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        Log.i("SplashViewModel", "Init")
        startup()
    }

    private fun startup() {
        if (storage.getIdToken().isNotEmpty()) {
            Log.i("SplashViewModel", "Navigating to MenuActivity")
            navigationDispatcher.navigateTo(
                MainDests.Tables
            )
        } else {
            Log.i("SplashViewModel", "Navigating to InitActivity")
            startConfiguring()
        }
    }

    private fun startConfiguring() {
        _isConfiguring.value = true
        configure(AppfinRestClientConfigure())
        viewModelScope.launch { _events.send(START_CONFIG) }
    }

    fun storePublicKey(token: String, tokenType: String) {
        //Guardar el token
        storage.putIdToken(token)
        storage.putTokenType(tokenType)
        viewModelScope.launch { _events.send(GET_MASTER_KEY) }
    }

    fun handleMasterKey(secretsList: ArrayList<SecretsV2>?) {
        if (secretsList != null) {
            //TODO: aca se debe verificar si el usuario esta logeado en Avoqado API
            navigationDispatcher.navigateTo(
                MainDests.Tables.route,
                navOptions = NavOptions.Builder()
                    .setPopUpTo(MainDests.Splash.route, inclusive = true)
                    .build()
            )
        } else {
            Log.i(TAG, "Inyección de llaves ERROR")
        }
    }
}