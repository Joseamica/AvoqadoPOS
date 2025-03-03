package com.avoqado.pos.features.authorization.presentation.splash

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavOptions
import com.avoqado.pos.AppfinRestClientConfigure
import com.avoqado.pos.core.presentation.navigation.NavigationDispatcher
import com.avoqado.pos.core.data.local.SessionManager
import com.avoqado.pos.core.data.network.AvoqadoAPI
import com.avoqado.pos.destinations.MainDests
import com.avoqado.pos.features.management.presentation.navigation.ManagementDests
import com.avoqado.pos.views.InitActivity.Companion.TAG
import com.menta.android.keys.admin.core.response.keys.SecretsV2
import com.menta.android.restclient.core.RestClientConfiguration.configure
import com.menta.android.restclient.core.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class SplashViewModel constructor(
    private val navigationDispatcher: NavigationDispatcher,
    private val storage: Storage,
    private val serialNumber: String,
    private val sessionManager: SessionManager
) : ViewModel() {

    val operationPreference = sessionManager.getOperationPreference()

    companion object {
        val START_CONFIG = "START_CONFIG"
        val GET_MASTER_KEY = "GET_MASTER_KEY"
        val REFRESH_CONFIG = "REFRESH_CONFIG"
    }

    private val _isConfiguring = MutableStateFlow(false)
    val isConfiguring: StateFlow<Boolean> = _isConfiguring.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _events = Channel<String>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    val currentUser = sessionManager.getAvoqadoSession()

    init {
        Log.i("SplashViewModel", "Init with serial number -> $serialNumber")
        viewModelScope.launch (Dispatchers.IO) {
            try {
                val result = AvoqadoAPI.apiService.getTPV(serialNumber)
                result.venueId?.let {
                    sessionManager.saveVenueId(it)
                }
            } catch (e: Exception) {
                Log.e("SplashViewModel", "Error fetching TPV", e)
            }
        }

        startup()
    }

    private fun startup() {
        if (storage.getIdToken().isNotEmpty()) {
            getTerminalInfo(serialNumber)
        } else {
            Log.i("SplashViewModel", "Navigating to InitActivity")
            startConfiguring()
        }
    }

    private fun getTerminalInfo(serial: String){
        viewModelScope.launch {
            try {
                Log.i("SplashViewModel", "Get Terminal Info -> token: ${storage.getTokenType()} ${storage.getIdToken()}")
                val terminals = AvoqadoAPI.mentaService.getTerminals("${storage.getTokenType()} ${storage.getIdToken()}")
                val currentTerminal = terminals.embedded.terminals?.firstOrNull { terminal -> terminal.serialCode == serial }
                //TODO: Guardar terminal en storage
                Log.i("SplashViewModel", "Terminal: $currentTerminal")
                currentTerminal?.let {
                    sessionManager.saveTerminalInfo(it)
                }
                Log.i("SplashViewModel", "Navigating to MenuActivity")
                navigationDispatcher.navigateTo(
                    ManagementDests.Tables
                )
            }
            catch (e: Exception) {
                Log.e("SplashViewModel", "Error fetching terminals", e)
                if (e is HttpException) {
                    if (e.code() == 401) {
                        Log.i("SplashViewModel", "Unauthorized")
                        _isRefreshing.value = true
                        startConfiguring()
                    }
                }
            }
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

            if (_isRefreshing.value) {
                getTerminalInfo(serialNumber)
            } else {
                navigationDispatcher.navigateTo(
                    ManagementDests.Tables.route,
                    navOptions = NavOptions.Builder()
                        .setPopUpTo(MainDests.Splash.route, inclusive = true)
                        .build()
                )
            }

        } else {
            Log.i(TAG, "Inyección de llaves ERROR")
        }
    }
}