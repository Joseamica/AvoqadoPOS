package com.avoqado.pos.features.management.presentation.shiftNotStarted

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avoqado.pos.core.data.local.SessionManager
import com.avoqado.pos.core.domain.models.AvoqadoError
import com.avoqado.pos.core.domain.repositories.TerminalRepository
import com.avoqado.pos.core.presentation.delegates.SnackbarDelegate
import com.avoqado.pos.core.presentation.navigation.NavigationDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ShiftNotStartedViewModel constructor(
    private val navigationDispatcher: NavigationDispatcher,
    private val terminalRepository: TerminalRepository,
    private val snackbarDelegate: SnackbarDelegate,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun onBack() {
        navigationDispatcher.navigateBack()
    }

    fun onOpenShift() {
        viewModelScope.launch {
            sessionManager.getVenueInfo()?.let {
                _isLoading.update { true }
                try {
                    val shift = terminalRepository.startTerminalShift(
                        venueId = it.id ?: "",
                        posName = it.posName ?: ""
                    )
                    sessionManager.setShift(shift)
                    navigationDispatcher.navigateBack()
                } catch (e: Exception) {
                    if (e is AvoqadoError.BasicError) {
                        snackbarDelegate.showSnackbar(
                            message = e.message
                        )
                    }
                } finally {
                    _isLoading.update { false }
                }
            } ?: run {
                snackbarDelegate.showSnackbar(
                    message = "No hay informacion del POS name."
                )
            }
        }
    }
}