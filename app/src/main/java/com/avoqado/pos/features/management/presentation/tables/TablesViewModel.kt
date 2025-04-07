package com.avoqado.pos.features.management.presentation.tables

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avoqado.pos.core.presentation.navigation.NavigationArg
import com.avoqado.pos.core.presentation.navigation.NavigationDispatcher
import com.avoqado.pos.core.data.local.SessionManager
import com.avoqado.pos.core.data.network.AvoqadoAPI
import com.avoqado.pos.core.data.network.models.NetworkTable
import com.avoqado.pos.core.data.network.models.NetworkVenue
import com.avoqado.pos.core.domain.repositories.TerminalRepository
import com.avoqado.pos.core.presentation.delegates.SnackbarDelegate
import com.avoqado.pos.core.presentation.destinations.MainDests
import com.avoqado.pos.features.management.domain.ManagementRepository
import com.avoqado.pos.features.management.presentation.home.models.Table
import com.avoqado.pos.features.management.presentation.navigation.ManagementDests
import com.avoqado.pos.features.payment.presentation.navigation.PaymentDests
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    fun toggleSettingsModal(value: Boolean) {
        _showSettings.update {
            value
        }
    }



    init {
        fetchTables()
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
                Log.e("HomeViewModel", "Error fetching tables", e)
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
                            message = "Se ha iniciado un nuevo turno."
                        )
                    }
                }
            } catch (e: Exception) {
                Timber.e(e)
            } finally {
                _isLoading.update {
                    false
                }
            }
        }
    }

}