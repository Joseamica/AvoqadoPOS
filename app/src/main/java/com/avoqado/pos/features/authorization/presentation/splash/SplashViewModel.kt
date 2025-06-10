package com.avoqado.pos.features.authorization.presentation.splash


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avoqado.pos.core.data.local.SessionManager
import com.avoqado.pos.core.data.network.AvoqadoAPI
import com.avoqado.pos.core.data.network.models.NetworkVenue
import com.avoqado.pos.core.domain.repositories.TerminalRepository
import com.avoqado.pos.core.presentation.delegates.SnackbarDelegate
import com.avoqado.pos.core.presentation.delegates.SnackbarState
import com.avoqado.pos.core.presentation.destinations.MainDests
import com.avoqado.pos.core.presentation.navigation.NavigationDispatcher
import com.avoqado.pos.features.management.domain.ManagementRepository
import com.avoqado.pos.features.management.presentation.navigation.ManagementDests
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class SplashViewModel constructor(
    private val navigationDispatcher: NavigationDispatcher,
    private val serialNumber: String,
    private val sessionManager: SessionManager,
    private val snackbarDelegate: SnackbarDelegate,
    private val terminalRepository: TerminalRepository,
    private val managementRepository: ManagementRepository,
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
    var venueInfo: NetworkVenue? = null

    fun initSplash() {
        Timber.i("Init with serial number -> $serialNumber")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = AvoqadoAPI.apiService.getTPV(serialNumber)

                result.venueId?.let {
                    sessionManager.saveVenueId(it)
                    val venue = managementRepository.getVenue(it)
                    sessionManager.saveVenueInfo(venue)
                    venueInfo = venue

                    val shift =
                        terminalRepository.getTerminalShift(
                            venueId = it,
                            posName = venue.posName ?: "",
                        )
                    sessionManager.setShift(shift)

                    if (currentUser == null) {
                        navigationDispatcher.navigateWithArgs(MainDests.SignIn)
                    } else {
                        navigationDispatcher.navigateTo(
                            ManagementDests.Home,
                        )
                    }
                } ?: run {
                    snackbarDelegate.showSnackbar(
                        state = SnackbarState.Default,
                        message = "No tienes asignado un restaurante a este terminal.",
                    )
                }
            } catch (e: Exception) {
                Timber.e("Error fetching TPV", e)
                if (sessionManager.getVenueId().isNotEmpty()) {
                    if (currentUser == null) {
                        navigationDispatcher.navigateTo(MainDests.SignIn)
                    } else {
                        navigationDispatcher.navigateTo(
                            ManagementDests.Home,
                        )
                    }
                }
            }
        }
    }

    private fun startConfiguring() {
        _isConfiguring.value = true
        viewModelScope.launch {
            _events.send(START_CONFIG)
        }
    }
}
