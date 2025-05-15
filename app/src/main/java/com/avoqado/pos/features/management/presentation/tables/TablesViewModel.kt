package com.avoqado.pos.features.management.presentation.tables

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.withContext
import com.avoqado.pos.AvoqadoApp
import com.avoqado.pos.core.data.local.SessionManager
import com.avoqado.pos.core.data.network.SocketIOManager
import com.avoqado.pos.core.domain.mappers.ShiftMapper
import com.avoqado.pos.core.domain.models.Shift
import com.avoqado.pos.core.domain.repositories.TerminalRepository
import com.avoqado.pos.core.presentation.delegates.SnackbarDelegate
import com.avoqado.pos.core.presentation.delegates.SnackbarState
import com.avoqado.pos.core.presentation.destinations.MainDests
import com.avoqado.pos.core.presentation.navigation.NavigationArg
import com.avoqado.pos.core.presentation.navigation.NavigationDispatcher
import com.avoqado.pos.features.management.domain.ManagementRepository
import com.avoqado.pos.features.management.presentation.navigation.ManagementDests
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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
    private val terminalRepository: TerminalRepository,
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

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _shiftStarted = MutableStateFlow(currentShift != null)
    val shiftStarted: StateFlow<Boolean> = _shiftStarted.asStateFlow()

    // Track WebSocket connection status
    private val _isWebSocketConnected = MutableStateFlow(false)

    // Track if we're currently collecting WebSocket events to avoid multiple collectors
    private var isCollectingSocketEvents = false
    private var isCollectingShiftEvents = false
    
    // Job for debouncing refresh calls
    private var fetchTablesJob: Job? = null
    
    // Timestamp of last refresh to avoid excessive API calls
    private var lastFetchTimestamp = 0L
    private val MINIMUM_REFRESH_INTERVAL = 2000L // 2 seconds minimum between refreshes

    // Get a reference to the socket service
    private val socketService by lazy { AvoqadoApp.socketService }

    init {
        fetchTables()
    }

    override fun onCleared() {
        super.onCleared()
        // Don't disconnect from sockets here anymore - the service handles the connection lifecycle
        fetchTablesJob?.cancel()
    }

    fun toggleSettingsModal(value: Boolean) {
        _showSettings.update {
            value
        }
    }

    fun onPullToRefreshTrigger() {
        viewModelScope.launch {
            _isRefreshing.update { true }
            debouncedFetchTables(forceRefresh = true)
            _isRefreshing.update { false }
        }
    }
    fun isOrderingFeatureEnabled(): Boolean {
        return sessionManager.getVenueInfo()?.feature?.ordering ?: false
    }
    fun startListeningForVenueUpdates() {
        if (venueId.isEmpty()) {
            Log.e("TablesViewModel", "Cannot listen for updates: venueId is empty")
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Join the venue room via the SocketService
                socketService?.joinVenueRoom(venueId)
                _isWebSocketConnected.update { true }

                // Use flatMapLatest to avoid multiple collectors
                if (!isCollectingSocketEvents) {
                    isCollectingSocketEvents = true

                    // Collect venue-level events
                    socketService?.venueMessageFlow?.collectLatest { update ->
                        Log.d("TablesViewModel", "Received venue update: $update")

                        val shouldRefresh =
                            when (update.status?.uppercase()) {
                                "OPEN", "DELETED", "PAID", "CANCELED", "PAYMENT_ADDED", "UPDATED" -> true
                                else -> false
                            }

                        if (shouldRefresh) {
                            Log.d("TablesViewModel", "Requesting tables refresh")
                            debouncedFetchTables()

                            // Notify for significant events
                            if (update.status?.uppercase() == "OPEN") {
                                snackbarDelegate.showSnackbar(
                                    state = SnackbarState.Default,
                                    message = "Nueva cuenta creada: Mesa ${update.tableNumber ?: "?"}",
                                )
                            }
                        }
                    }
                }

                // Also start listening for shift events
                startListeningForShiftEvents()
            } catch (e: Exception) {
                Log.e("TablesViewModel", "Error setting up venue updates", e)
                _isWebSocketConnected.update { false }
                isCollectingSocketEvents = false
            }
        }
    }
    
    /**
     * Debounced version of fetchTables to prevent rapid API calls
     */
    private fun debouncedFetchTables(forceRefresh: Boolean = false) {
        val currentTime = System.currentTimeMillis()
        val timeSinceLastFetch = currentTime - lastFetchTimestamp
        
        // If we're already loading or if it's too soon since the last refresh and not forced
        if ((_isLoading.value || timeSinceLastFetch < MINIMUM_REFRESH_INTERVAL) && !forceRefresh) {
            Log.d("TablesViewModel", "Skipping refresh: already loading or too soon (${timeSinceLastFetch}ms since last refresh)")
            return
        }
        
        // Cancel any pending refresh job
        fetchTablesJob?.cancel()
        
        // Start a new debounced job
        fetchTablesJob = viewModelScope.launch {
            delay(300) // 300ms debounce delay
            fetchTables()
            lastFetchTimestamp = System.currentTimeMillis()
        }
    }

    private fun startListeningForShiftEvents() {
        if (venueId.isEmpty()) {
            Log.e("TablesViewModel", "Cannot listen for shift updates: venueId is empty")
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (!isCollectingShiftEvents) {
                    isCollectingShiftEvents = true

                    socketService?.shiftMessageFlow?.collectLatest { shiftUpdateMessage ->
                        Log.d("TablesViewModel", "Received shift update: $shiftUpdateMessage")

                        // Map the network model to domain model
                        val shift = socketService?.mapShiftToDomain(shiftUpdateMessage)
                        
                        if (shift != null) {
                            // Update the current shift
                            currentShift = shift

                            // Update shift status
                            _shiftStarted.update { shift.isStarted }

                            // Notify the user about the change
                            if (shift.isStarted && !shift.isFinished) {
                                snackbarDelegate.showSnackbar(
                                    state = SnackbarState.Default,
                                    message = "Se ha iniciado un nuevo turno.",
                                )
                            } else if (shift.isFinished) {
                                snackbarDelegate.showSnackbar(
                                    state = SnackbarState.Default,
                                    message = "El turno ha sido cerrado.",
                                )
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("TablesViewModel", "Error setting up shift updates", e)
                isCollectingShiftEvents = false
            }
        }
    }

    private fun stopListeningForShiftEvents() {
        // We don't need to disconnect - the SocketService maintains the connection
        isCollectingShiftEvents = false
    }

    fun stopListeningForVenueUpdates() {
        // We don't need to leave the venue room here - the SocketService maintains the connection
        isCollectingSocketEvents = false
        stopListeningForShiftEvents()
    }

    fun fetchTables() {
        // Don't start a new fetch if we're already loading
        if (_isLoading.value) {
            Log.d("TablesViewModel", "Skipping fetch: already loading")
            return
        }
        
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.update {
                true
            }
            try {
                Log.d("TablesViewModel", "Fetching tables from API")
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
                venueInfo?.id ?: "undefined",
            ),
            NavigationArg.StringArg(
                ManagementDests.TableDetail.ARG_TABLE_ID,
                billId,
            ),
        )
    }

    fun onBackAction() {
        navigationDispatcher.navigateBack()
    }

    fun logout() {
        // No need to clean up WebSocket before logout - the service handles the connection
        sessionManager.clearAvoqadoSession()
        navigationDispatcher.popToDestination(MainDests.Splash, inclusive = true)
        navigationDispatcher.navigateWithArgs(
            MainDests.SignIn,
            NavigationArg.StringArg(
                MainDests.SignIn.ARG_REDIRECT,
                ManagementDests.Home.route,
            ),
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
                            posName = it.posName ?: "",
                        )
                        sessionManager.clearShift()
                        currentShift = null
                        _shiftStarted.update { false }
                        snackbarDelegate.showSnackbar(
                            state = SnackbarState.Default,
                            message = "El turno ha sido cerrado.",
                        )
                    } else {
                        val shift =
                            terminalRepository.startTerminalShift(
                                venueId = it.id ?: "",
                                posName = it.posName ?: "",
                            )
                        sessionManager.setShift(shift)
                        currentShift = shift
                        _shiftStarted.update { true }
                        snackbarDelegate.showSnackbar(
                            state = SnackbarState.Default,
                            message = "Se ha iniciado un nuevo turno.",
                        )
                    }
                }
            } catch (e: Exception) {
                Timber.e(e)
                snackbarDelegate.showSnackbar(
                    state = SnackbarState.Default,
                    message = "Error: ${e.message ?: "Ocurrió un error inesperado"}",
                )
            } finally {
                _isLoading.update {
                    false
                }
            }
        }
    }
    
    /**
     * Creates a new bill with the specified name
     * 
     * @param billName The name to assign to the new bill
     */
    fun createNewBill(billName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.update { true }
            try {
                val venueId = sessionManager.getVenueId()
                if (venueId.isEmpty()) {
                    snackbarDelegate.showSnackbar(
                        state = SnackbarState.Default,
                        message = "No se pudo crear la cuenta: falta el ID del establecimiento"
                    )
                    return@launch
                }
                
                if (currentShift == null) {
                    // Show error if no shift is active
                    withContext(Dispatchers.Main) {
                        snackbarDelegate.showSnackbar(
                            state = SnackbarState.Default,
                            message = "No se pudo crear la cuenta: no hay un turno iniciado"
                        )
                    }
                    return@launch
                }
                
                // Call repository to create a new bill
                val result = managementRepository.createNewBill(venueId, billName)
                
                // Show success message
                withContext(Dispatchers.Main) {
                    if (result) {
                        snackbarDelegate.showSnackbar(
                            state = SnackbarState.Default,
                            message = "Cuenta '${billName}' creada exitosamente"
                        )
                        // Refresh the tables list
                        fetchTables()
                    } else {
                        snackbarDelegate.showSnackbar(
                            state = SnackbarState.Default,
                            message = "No se pudo crear la cuenta"
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("TablesViewModel", "Error creating new bill", e)
                withContext(Dispatchers.Main) {
                    snackbarDelegate.showSnackbar(
                        state = SnackbarState.Default,
                        message = "Error: ${e.message ?: "Ocurrió un error inesperado"}"
                    )
                }
            } finally {
                _isLoading.update { false }
            }
        }
    }
}
