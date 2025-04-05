package com.avoqado.pos.features.authorization.presentation.splash

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavOptions
import com.avoqado.pos.AppfinRestClientConfigure
import com.avoqado.pos.core.presentation.navigation.NavigationDispatcher
import com.avoqado.pos.core.data.local.SessionManager
import com.avoqado.pos.core.data.network.AvoqadoAPI
import com.avoqado.pos.core.domain.repositories.TerminalRepository
import com.avoqado.pos.core.presentation.delegates.SnackbarDelegate
import com.avoqado.pos.core.presentation.delegates.SnackbarState
import com.avoqado.pos.core.presentation.destinations.MainDests
import com.avoqado.pos.features.management.domain.ManagementRepository
import com.avoqado.pos.features.management.presentation.navigation.ManagementDests
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
import timber.log.Timber

class SplashViewModel constructor(
    private val navigationDispatcher: NavigationDispatcher,
    private val storage: Storage,
    private val serialNumber: String,
    private val sessionManager: SessionManager,
    private val snackbarDelegate: SnackbarDelegate,
    private val terminalRepository: TerminalRepository,
    private val managementRepository: ManagementRepository
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
        Timber.i("Init with serial number -> $serialNumber")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = AvoqadoAPI.apiService.getTPV(serialNumber)

                result.venueId?.let {
                    sessionManager.saveVenueId(it)
                    val venue = managementRepository.getVenue(it)
                    sessionManager.saveVenueInfo(venue)

                    val shift = terminalRepository.getTerminalShift(
                        venueId = it,
                        posName = venue.posName?:""
                    )
                    sessionManager.setShift(shift)

                    if (currentUser == null) {
                        navigationDispatcher.navigateWithArgs(MainDests.SignIn)
                    } else {
                        startup()
                    }
                } ?: run {
                    snackbarDelegate.showSnackbar(
                        state = SnackbarState.Default,
                        message = "No tienes asignado un restaurante a este terminal."
                    )
                }
            } catch (e: Exception) {
                Timber.e("Error fetching TPV", e)
                if (sessionManager.getVenueId().isNotEmpty()) {
                    if (currentUser == null) {
                        navigationDispatcher.navigateTo(MainDests.SignIn)
                    } else {
                        startup()
                    }
                }
            }
        }


    }

    private fun startup() {
        if (storage.getIdToken().isNotEmpty()) {
            getTerminalInfo(serialNumber)
        } else {
            startConfiguring()
        }
    }

    private fun getTerminalInfo(serial: String) {
        viewModelScope.launch {
            try {
                val terminals =
                    AvoqadoAPI.mentaService.getTerminals("${storage.getTokenType()} ${storage.getIdToken()}")
                val currentTerminal =
                    terminals.embedded.terminals?.firstOrNull { terminal -> terminal.serialCode == serial }

                currentTerminal?.let {
                    sessionManager.saveTerminalInfo(it)
                }

                navigationDispatcher.navigateTo(
                    ManagementDests.Home
                )
            } catch (e: Exception) {
                Timber.e("Error fetching terminals", e)
                if (e is HttpException) {
                    if (e.code() == 401) {
                        Timber.i("Unauthorized")
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
        currentUser?.apiKey?.let {
            storage.putMerchantApiKey(it)
        }
        viewModelScope.launch {
            _events.send(START_CONFIG)
        }
    }

    fun storePublicKey(token: String, tokenType: String) {
        storage.putIdToken(token)
        storage.putTokenType(tokenType)
        viewModelScope.launch {
            _events.send(GET_MASTER_KEY)
        }
    }

    fun handleMasterKey(secretsList: ArrayList<SecretsV2>?) {
        if (secretsList != null) {
            //TODO: aca se debe verificar si el usuario esta logeado en Avoqado API

            if (_isRefreshing.value) {
                getTerminalInfo(serialNumber)
            } else {
                navigationDispatcher.navigateTo(
                    ManagementDests.Home.route,
                    navOptions = NavOptions.Builder()
                        .setPopUpTo(MainDests.Splash.route, inclusive = true)
                        .build()
                )
            }
        } else {
            Timber.e("SplashViewModel ", "Inyección de llaves ERROR")
        }
    }
}