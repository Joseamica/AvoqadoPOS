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
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import retrofit2.HttpException
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
        const val TPV_NOT_FOUND_RETRY_DELAY_MS = 15000L // 15 seconds
    }

    private val _isConfiguring = MutableStateFlow(false)
    val isConfiguring: StateFlow<Boolean> = _isConfiguring.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()
    
    private val _isTpvNotFound = MutableStateFlow(false)
    val isTpvNotFound: StateFlow<Boolean> = _isTpvNotFound.asStateFlow()
    
    private val _retryCountdown = MutableStateFlow(TPV_NOT_FOUND_RETRY_DELAY_MS / 1000)
    val retryCountdown: StateFlow<Long> = _retryCountdown.asStateFlow()
    
    private var countdownJob: Job? = null

    private val _events = Channel<String>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    val currentUser = sessionManager.getAvoqadoSession()
    var venueInfo: NetworkVenue? = null

    fun initSplash() {
        // Cancel any existing countdown job before starting new fetch
        countdownJob?.cancel()
        _isTpvNotFound.value = false
        _isConfiguring.value = true
        
        Timber.i("Init with serial number -> $serialNumber")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = AvoqadoAPI.apiService.getTPV(serialNumber)

                // TPV fetch was successful, reset state
                _isTpvNotFound.value = false
                
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
                
                // Handle the specific 404 error case (TPV not found)
                if (e is HttpException && e.code() == 404) {
                    _isTpvNotFound.value = true
                    startRetryCountdown()
                } else if (sessionManager.getVenueId().isNotEmpty()) {
                    // For other errors, proceed if we have a venueId saved
                    if (currentUser == null) {
                        navigationDispatcher.navigateTo(MainDests.SignIn)
                    } else {
                        navigationDispatcher.navigateTo(
                            ManagementDests.Home,
                        )
                    }
                }
            } finally {
                // Only set configuring to false if we're not in TPV not found state
                if (!_isTpvNotFound.value) {
                    _isConfiguring.value = false
                }
            }
        }
    }
    
    private fun startRetryCountdown() {
        // Reset and start the countdown
        _retryCountdown.value = TPV_NOT_FOUND_RETRY_DELAY_MS / 1000
        
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            while (_retryCountdown.value > 0 && isActive) {
                delay(1000) // Wait for 1 second
                _retryCountdown.value = _retryCountdown.value - 1
            }
            
            // When countdown reaches 0, retry the TPV fetch
            if (isActive) {
                initSplash()
            }
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        countdownJob?.cancel()
    }

    private fun startConfiguring() {
        _isConfiguring.value = true
        viewModelScope.launch {
            _events.send(START_CONFIG)
        }
    }
}
