package com.avoqado.pos.features.management.presentation.tables

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avoqado.pos.core.presentation.navigation.NavigationArg
import com.avoqado.pos.core.presentation.navigation.NavigationDispatcher
import com.avoqado.pos.core.data.local.SessionManager
import com.avoqado.pos.core.data.network.SocketIOManager
import com.avoqado.pos.core.domain.repositories.TerminalRepository
import com.avoqado.pos.core.presentation.delegates.SnackbarDelegate
import com.avoqado.pos.core.presentation.delegates.SnackbarState
import com.avoqado.pos.core.presentation.destinations.MainDests
import com.avoqado.pos.features.management.domain.ManagementRepository
import com.avoqado.pos.features.management.presentation.navigation.ManagementDests
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

class TablesViewModel(
    private val navigationDispatcher: NavigationDispatcher,
    private val sessionManager: SessionManager,
    private val managementRepository: ManagementRepository,
    private val snackbarDelegate: SnackbarDelegate,
    private val terminalRepository: TerminalRepository
) : ViewModel() {
    val venueId = sessionManager.getVenueId()
    val venueInfo = sessionManager.getVenueInfo()
    var currentShift = sessionManager.getShift()

    private val _tables = MutableStateFlow(listOf<Pair<String, String>>())
    val tables: StateFlow<List<Pair<String, String>>> = _tables.asStateFlow()

    private val _showSettings = MutableStateFlow(false)
    val showSettings: StateFlow<Boolean> = _showSettings.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _shiftStarted = MutableStateFlow(currentShift != null)
    val shiftStarted: StateFlow<Boolean> = _shiftStarted.asStateFlow()

    // Track WebSocket connection status
    private val _isWebSocketConnected = MutableStateFlow(false)

    // Track if we're currently collecting WebSocket events to avoid multiple collectors
    private var isCollectingSocketEvents = false

    init {
        fetchTables()
    }

    override fun onCleared() {
        super.onCleared()
        // Ensure we clean up WebSocket resources when ViewModel is destroyed
        stopListeningForVenueUpdates()
    }

    fun toggleSettingsModal(value: Boolean) {
        _showSettings.update {
            value
        }
    }

    fun startListeningForVenueUpdates() {
        if (venueId.isEmpty()) {
            Log.e("TablesViewModel", "Cannot listen for updates: venueId is empty")
            return
        }

        // Avoid duplicate connections
        if (_isWebSocketConnected.value) {
            Log.d("TablesViewModel", "WebSocket already connected, skipping connection")
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d("TablesViewModel", "Starting WebSocket connection to venue: $venueId")

                // Connect to venue-level socket room
                SocketIOManager.connect(SocketIOManager.getServerUrl())
                SocketIOManager.joinMobileRoom(venueId)

                _isWebSocketConnected.update { true }

                // Only set up one collector
                if (!isCollectingSocketEvents) {
                    isCollectingSocketEvents = true

                    // Listen for venue updates
                    managementRepository.listenVenueEvents().collectLatest { update ->
                        Log.d("TablesViewModel", "Received venue update: $update")

                        // Refresh tables when a bill status changes
                        when (update.status?.uppercase()) {
                            "OPEN" -> {
                                Log.d("TablesViewModel", "New bill opened, refreshing tables")
                                snackbarDelegate.showSnackbar(
                                    state = SnackbarState.Default,
                                    message = "Nueva cuenta creada: Mesa ${update.tableNumber}"
                                )
                                fetchTables()
                            }
                            "DELETED", "PAID", "CANCELED" -> {
                                Log.d("TablesViewModel", "Bill ${update.status}, refreshing tables")
                                fetchTables()
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("TablesViewModel", "Error setting up venue updates", e)
                _isWebSocketConnected.update { false }
                isCollectingSocketEvents = false
            }
        }
    }

    fun stopListeningForVenueUpdates() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (_isWebSocketConnected.value) {
                    Log.d("TablesViewModel", "Disconnecting from venue: $venueId")
                    SocketIOManager.leaveMobileRoom(venueId)
                    _isWebSocketConnected.update { false }
                }
                isCollectingSocketEvents = false
            } catch (e: Exception) {
                Log.e("TablesViewModel", "Error stopping venue updates", e)
            }
        }
    }

    fun fetchTables() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.update {
                true
            }
            try {
                managementRepository.getActiveBills(venueId).let { result ->
                    _tables.update {
                        result
                    }
                }
            } catch (e: Exception) {
                Log.e("TablesViewModel", "Error fetching tables", e)
            } finally {
                _isLoading.update { false }
            }
        }
    }

    fun onTableSelected(billId: String) {
        navigationDispatcher.navigateWithArgs(
            ManagementDests.TableDetail,
            NavigationArg.StringArg(
                ManagementDests.TableDetail.ARG_VENUE_ID,
                venueInfo?.id ?: "undefined"
            ),
            NavigationArg.StringArg(
                ManagementDests.TableDetail.ARG_TABLE_ID,
                billId
            )
        )
    }

    fun onBackAction() {
        navigationDispatcher.navigateBack()
    }

    fun logout() {
        // Clean up WebSocket before logout
        stopListeningForVenueUpdates()

        sessionManager.clearAvoqadoSession()
        navigationDispatcher.popToDestination(MainDests.Splash, inclusive = true)
        navigationDispatcher.navigateWithArgs(
            MainDests.SignIn,
            NavigationArg.StringArg(
                MainDests.SignIn.ARG_REDIRECT,
                ManagementDests.Home.route
            )
        )
    }

    fun toggleShift() {
        viewModelScope.launch {
            try {
                toggleSettingsModal(false)
                _isLoading.update {
                    true
                }
                val venue = sessionManager.getVenueInfo()
                venue?.let {
                    if (currentShift != null) {
                        terminalRepository.closeTerminalShift(
                            venueId = it.id ?: "",
                            posName = it.posName ?: ""
                        )
                        sessionManager.clearShift()
                        currentShift = null
                        _shiftStarted.update { false }
                        snackbarDelegate.showSnackbar(
                            state = SnackbarState.Default,
                            message = "El turno ha sido cerrado."
                        )
                    } else {
                        val shift = terminalRepository.startTerminalShift(
                            venueId = it.id ?: "",
                            posName = it.posName ?: ""
                        )
                        sessionManager.setShift(shift)
                        currentShift = shift
                        _shiftStarted.update { true }
                        snackbarDelegate.showSnackbar(
                            state = SnackbarState.Default,
                            message = "Se ha iniciado un nuevo turno."
                        )
                    }
                }
            } catch (e: Exception) {
                Timber.e(e)
                snackbarDelegate.showSnackbar(
                    state = SnackbarState.Default,
                    message = "Error: ${e.message ?: "Ocurrió un error inesperado"}"
                )
            } finally {
                _isLoading.update {
                    false
                }
            }
        }
    }
}