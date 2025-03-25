package com.avoqado.pos.features.management.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avoqado.pos.core.presentation.navigation.NavigationArg
import com.avoqado.pos.core.presentation.navigation.NavigationDispatcher
import com.avoqado.pos.core.data.local.SessionManager
import com.avoqado.pos.core.domain.repositories.TerminalRepository
import com.avoqado.pos.destinations.MainDests
import com.avoqado.pos.features.management.presentation.navigation.ManagementDests
import com.avoqado.pos.features.payment.presentation.navigation.PaymentDests
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

class HomeViewModel(
    private val navigationDispatcher: NavigationDispatcher,
    private val sessionManager: SessionManager,
    private val terminalRepository: TerminalRepository
) : ViewModel() {

    val currentSession = sessionManager.getAvoqadoSession()
    var currentShift = sessionManager.getShift()

    private val _showSettings = MutableStateFlow(false)
    val showSettings: StateFlow<Boolean> = _showSettings.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        viewModelScope.launch {
            currentSession?.let {
//                terminalRepository.getTerminalShift(it.venueId)
            }

        }
    }

    fun toggleSettingsModal(value: Boolean) {
        _showSettings.update {
            value
        }
    }

    fun goToSummary() {
        navigationDispatcher.navigateTo(PaymentDests.TransactionsSummary)
    }

    fun goToNewPayment() {
        navigationDispatcher.navigateTo(ManagementDests.VenueTables)
    }

    fun goToQuickPayment() {
        navigationDispatcher.navigateTo(PaymentDests.QuickPayment)
    }

    fun goToShowPayments() {
        navigationDispatcher.navigateTo(PaymentDests.TransactionsSummary)
    }

    fun goToShowShifts() {
        navigationDispatcher.navigateTo(PaymentDests.TransactionsSummary)
    }

    fun logout() {
        _showSettings.update {
            false
        }
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
                    } else {
                        val shift = terminalRepository.startTerminalShift(
                            venueId = it.id ?: "",
                            posName = it.posName ?: ""
                        )
                        sessionManager.setShift(shift)
                        currentShift = shift
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