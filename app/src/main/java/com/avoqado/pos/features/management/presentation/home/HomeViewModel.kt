package com.avoqado.pos.features.management.presentation.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avoqado.pos.core.data.local.SessionManager
import com.avoqado.pos.core.data.network.SocketIOManager
import com.avoqado.pos.core.domain.repositories.TerminalRepository
import com.avoqado.pos.core.presentation.delegates.SnackbarDelegate
import com.avoqado.pos.core.presentation.destinations.MainDests
import com.avoqado.pos.core.presentation.navigation.NavigationArg
import com.avoqado.pos.core.presentation.navigation.NavigationDispatcher
import com.avoqado.pos.features.management.presentation.navigation.ManagementDests
import com.avoqado.pos.features.payment.presentation.navigation.PaymentDests
import com.avoqado.pos.features.payment.presentation.transactions.SummaryTabs
import kotlinx.coroutines.delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

class HomeViewModel(
    private val navigationDispatcher: NavigationDispatcher,
    private val sessionManager: SessionManager,
    private val terminalRepository: TerminalRepository,
    private val snackbarDelegate: SnackbarDelegate,
) : ViewModel() {
    val currentSession = sessionManager.getAvoqadoSession()
    var currentShift = sessionManager.getShift()

    private val _shiftStarted = MutableStateFlow(currentShift != null)
    val shiftStarted: StateFlow<Boolean> = _shiftStarted.asStateFlow()

    private val _showSettings = MutableStateFlow(false)
    val showSettings: StateFlow<Boolean> = _showSettings.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    // Track if we're currently collecting shift events
    private var isCollectingShiftEvents = false

    init {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val venueId = sessionManager.getVenueId()
                val venue = sessionManager.getVenueInfo()

                if (venueId.isNotEmpty() && venue != null) {
                    // Forzar una actualización del turno actual desde el servidor
                    Timber.tag("HomeViewModel").d("Fetching current shift from server...")
                    currentShift =
                        terminalRepository.getTerminalShift(
                            venueId = venueId,
                            posName = venue.posName ?: "",
                        )

                    // Actualizar el estado basado en la información del servidor
                    _shiftStarted.update { currentShift != null && currentShift!!.isStarted }

                    Timber.d("Current shift after fetch: $currentShift, isStarted: ${_shiftStarted.value}")

                    // Luego iniciar la escucha de eventos
                    startListeningForShiftEvents()
                } else {
                    Timber.e("Cannot fetch current shift: venue info incomplete")
                }
            } catch (e: Exception) {
                Timber.e("Error fetching current shift", e)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopListeningForShiftEvents()
    }

    private fun startListeningForShiftEvents() {
        val venueId = sessionManager.getVenueId()
        if (venueId.isEmpty()) {
            Timber.e("Cannot listen for shift updates: venueId is empty")
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Conectar al repositorio para escuchar eventos de turnos
                terminalRepository.connectToShiftEvents(venueId)

                if (!isCollectingShiftEvents) {
                    isCollectingShiftEvents = true

                    terminalRepository.listenForShiftEvents().collectLatest { shift ->
                        Timber.d("Received shift update: $shift")

                        // Actualizamos el turno actual
                        currentShift = shift

                        // Actualizamos el estado del turno
                        _shiftStarted.update { shift.isStarted }

                        // Notificamos al usuario sobre el cambio
                        if (shift.isStarted && !shift.isFinished) {
                            snackbarDelegate.showSnackbar(
                                message = "Se ha iniciado un nuevo turno.",
                            )
                        } else if (shift.isFinished) {
                            snackbarDelegate.showSnackbar(
                                message = "El turno ha sido cerrado.",
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Error setting up shift updates")
                isCollectingShiftEvents = false
            }
        }
    }

    private fun stopListeningForShiftEvents() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                terminalRepository.disconnectFromShiftEvents()
                isCollectingShiftEvents = false
            } catch (e: Exception) {
                Timber.e(e, "Error stopping shift updates")
            }
        }
    }

    fun toggleSettingsModal(value: Boolean) {
        _showSettings.update {
            value
        }
    }

    fun goToSummary() {
        navigationDispatcher.navigateWithArgs(
            PaymentDests.TransactionsSummary,
            NavigationArg.StringArg(
                PaymentDests.TransactionsSummary.ARG_TAB,
                SummaryTabs.RESUMEN.name,
            ),
        )
    }

    fun goToNewPayment() {
        navigationDispatcher.navigateTo(ManagementDests.VenueTables)
    }

    fun goToQuickPayment() {
        navigationDispatcher.navigateTo(PaymentDests.QuickPayment)
    }

    fun goToShowPayments() {
        navigationDispatcher.navigateWithArgs(
            PaymentDests.TransactionsSummary,
            NavigationArg.StringArg(
                PaymentDests.TransactionsSummary.ARG_TAB,
                SummaryTabs.PAGOS.name,
            ),
        )
    }

    fun goToShowShifts() {
        navigationDispatcher.navigateWithArgs(
            PaymentDests.TransactionsSummary,
            NavigationArg.StringArg(
                PaymentDests.TransactionsSummary.ARG_TAB,
                SummaryTabs.TURNOS.name,
            ),
        )
    }
    
    fun goToMenuList() {
        val venueId = sessionManager.getVenueId()
        if (venueId.isNotEmpty()) {
            navigationDispatcher.navigateWithArgs(
                ManagementDests.MenuList,
                NavigationArg.StringArg(
                    ManagementDests.MenuList.ARG_VENUE_ID,
                    venueId,
                ),
            )
        }
    }

    fun logout() {
        _showSettings.update {
            false
        }
        // Detenemos la escucha de eventos de turnos antes de cerrar sesión
        stopListeningForShiftEvents()

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

    // Add a method to get the venue's posName
    fun getVenuePosName(): String? {
        return sessionManager.getVenueInfo()?.posName
    }
    
    fun isOrderingFeatureEnabled(): Boolean {
        return sessionManager.getVenueInfo()?.feature?.ordering ?: false
    }

    fun onPullToRefreshTrigger() {
        _isRefreshing.update { true }
        viewModelScope.launch {
            try {
                // Add a small delay to ensure the refresh spinner is visible
                delay(500)
                // This calls a function that launches its own coroutine
                forceRefreshShiftStatus()
                // Make sure we don't hide the spinner too quickly
                delay(500)
                Timber.d("Data refreshed successfully")
            } catch (e: Exception) {
                Timber.e("Error refreshing data", e)
            } finally {
                _isRefreshing.update { false }
            }
        }
    }

    fun toggleShift() {
        viewModelScope.launch {
            try {
                toggleSettingsModal(false)
                _isLoading.update { true }

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
                            message = "El turno ha sido cerrado.",
                        )
                    } else {
                        val shift = terminalRepository.startTerminalShift(
                            venueId = it.id ?: "",
                            posName = it.posName ?: "",
                        )
                        sessionManager.setShift(shift)
                        currentShift = shift
                        _shiftStarted.update { true }
                        snackbarDelegate.showSnackbar(
                            message = "Se ha iniciado un nuevo turno.",
                        )
                    }

                    // Refresh our socket connections
                    startListeningForShiftEvents()
                }
            } catch (e: Exception) {
                Timber.e(e)
            } finally {
                _isLoading.update { false }
            }
        }
    }

    private fun forceRefreshShiftStatus() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val venueId = sessionManager.getVenueId()
                val venue = sessionManager.getVenueInfo()

                if (venueId.isNotEmpty() && venue != null) {
                    // Forzar desconexión y reconexión
                    SocketIOManager.disconnect()
                    SocketIOManager.connect(SocketIOManager.getServerUrl())

                    // Esperar un momento para asegurar la conexión
                    delay(500)

                    // Forzar una actualización del turno desde el servidor
                    currentShift =
                        terminalRepository.getTerminalShift(
                            venueId = venueId,
                            posName = venue.posName ?: "",
                        )

                    // Actualizar el estado
                    _shiftStarted.update { currentShift != null && currentShift!!.isStarted }

                    Timber.d("Shift status refreshed: isStarted=${_shiftStarted.value}")

                    // Reconectar para escuchar eventos
                    startListeningForShiftEvents()
                }
            } catch (e: Exception) {
                Timber.e("Error refreshing shift status", e)
            }
        }
    }
}
