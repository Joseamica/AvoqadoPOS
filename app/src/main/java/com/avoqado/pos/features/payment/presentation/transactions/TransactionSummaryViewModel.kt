package com.avoqado.pos.features.payment.presentation.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avoqado.pos.core.data.local.SessionManager
import com.avoqado.pos.core.domain.models.AvoqadoError
import com.avoqado.pos.core.domain.models.PaymentShift
import com.avoqado.pos.core.domain.models.Shift
import com.avoqado.pos.core.domain.models.ShiftParams
import com.avoqado.pos.core.domain.models.ShiftSummary
import com.avoqado.pos.core.domain.repositories.TerminalRepository
import com.avoqado.pos.core.presentation.delegates.SnackbarDelegate
import com.avoqado.pos.core.presentation.navigation.NavigationDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.Instant

class TransactionSummaryViewModel(
    private val sessionManager: SessionManager,
    private val snackbarDelegate: SnackbarDelegate,
    private val navigationDispatcher: NavigationDispatcher,
    private val terminalRepository: TerminalRepository,
    private val initialTab: SummaryTabs,
) : ViewModel() {
    companion object {
        const val PAGE_SIZE = 10
    }

    val venue = sessionManager.getVenueInfo()

    private val _showWaiterSheet = MutableStateFlow<Boolean>(false)
    val showWaiterSheet: StateFlow<Boolean> = _showWaiterSheet.asStateFlow()

    private val _showCreatedSheet = MutableStateFlow<Boolean>(false)
    val showCreatedSheet: StateFlow<Boolean> = _showCreatedSheet.asStateFlow()

    private val _currentTab = MutableStateFlow<SummaryTabs>(initialTab)
    val currentTab: StateFlow<SummaryTabs> = _currentTab.asStateFlow()

    private val _filteredWaiters = MutableStateFlow<List<String>>(emptyList())
    val filteredWaiters: StateFlow<List<String>> = _filteredWaiters.asStateFlow()

    private val _filteredDates = MutableStateFlow<Pair<Long?, Long?>>(Pair(null, null))
    val filteredDates: StateFlow<Pair<Long?, Long?>> = _filteredDates.asStateFlow()

    private val _isLoadingMore = MutableStateFlow<Boolean>(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore.asStateFlow()

    private val _isLoading = MutableStateFlow<Boolean>(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _currentPage = MutableStateFlow<Int>(1)
    val currentPage: StateFlow<Int> = _currentPage.asStateFlow()

    private val _currenPaymenttPage = MutableStateFlow<Int>(1)
    val currenPaymenttPage: StateFlow<Int> = _currenPaymenttPage.asStateFlow()

    private val _shiftsList = MutableStateFlow<List<Shift>>(emptyList())
    val shiftsList: StateFlow<List<Shift>> = _shiftsList.asStateFlow()

    private val _shiftSummary = MutableStateFlow<ShiftSummary?>(null)
    val shiftSummary: StateFlow<ShiftSummary?> = _shiftSummary.asStateFlow()

    private val _paymentsShiftList = MutableStateFlow<List<PaymentShift>>(emptyList())
    val paymentsShiftList: StateFlow<List<PaymentShift>> = _paymentsShiftList.asStateFlow()

    val venueInfo = sessionManager.getVenueInfo()

// Add this state property to your ViewModel class
// Add this state property to your ViewModel class
    private val _hasMoreShiftsPages = MutableStateFlow<Boolean>(true)
    val hasMoreShiftsPages: StateFlow<Boolean> = _hasMoreShiftsPages.asStateFlow()

    fun loadShiftsSummary(nextPage: Boolean = false) {
        viewModelScope.launch {
            try {
                if (nextPage) {
                    _isLoadingMore.update { true }
                } else {
                    _isLoading.update { true }
                    // Reset hasMorePages when starting a new search
                    _hasMoreShiftsPages.update { true }
                }

                val params =
                    ShiftParams(
                        pageSize = PAGE_SIZE,
                        page =
                            if (nextPage) {
                                currentPage.value + 1
                            } else {
                                currentPage.value
                            },
                        venueId = venueInfo?.id ?: "",
                        waiterIds =
                            filteredWaiters.value.let {
                                if (it.isNotEmpty()) {
                                    it.joinToString(",")
                                } else {
                                    null
                                }
                            },
                        startTime =
                            filteredDates.value.first?.let {
                                val instant = Instant.ofEpochMilli(it)
                                instant.toString()
                            },
                        endTime =
                            filteredDates.value.second?.let {
                                val instant = Instant.ofEpochMilli(it)
                                instant.toString()
                            },
                    )

                val shifts = terminalRepository.getShiftSummary(params)

                // Check if there are more pages based on response size and meta data
                val hasNoMorePages = shifts.isEmpty() || shifts.size < PAGE_SIZE

                // Update hasMorePages state
                _hasMoreShiftsPages.update { !hasNoMorePages }

                _shiftsList.update {
                    if (nextPage) {
                        it + shifts
                    } else {
                        shifts
                    }
                }

                // Only increment page if we're doing pagination and we got new data
                if (nextPage && shifts.isNotEmpty()) {
                    _currentPage.update {
                        it + 1
                    }
                }
            } catch (e: Exception) {
                if (e is AvoqadoError) {
                    snackbarDelegate.showSnackbar(
                        message = e.message ?: "Algo salio mal...",
                    )
                } else {
                    Timber.e(e)
                }
                // If there's an error, assume no more pages
                _hasMoreShiftsPages.update { false }
            } finally {
                _isLoadingMore.update { false }
                _isLoading.update { false }
            }
        }
    }

// Add this state property to your ViewModel class
    private val _hasMorePaymentsPages = MutableStateFlow<Boolean>(true)
    val hasMorePaymentsPages: StateFlow<Boolean> = _hasMorePaymentsPages.asStateFlow()

    fun loadPaymentsSummary(nextPage: Boolean = false) {
        viewModelScope.launch {
            try {
                if (nextPage) {
                    _isLoadingMore.update { true }
                } else {
                    _isLoading.update { true }
                    // Reset hasMorePages when starting a new search
                    _hasMorePaymentsPages.update { true }
                }

                val payments =
                    terminalRepository.getShiftPaymentsSummary(
                        params =
                            ShiftParams(
                                pageSize = PAGE_SIZE,
                                page =
                                    if (nextPage) {
                                        currenPaymenttPage.value + 1
                                    } else {
                                        currenPaymenttPage.value
                                    },
                                venueId = venueInfo?.id ?: "",
                                waiterIds =
                                    filteredWaiters.value.let {
                                        if (it.isNotEmpty()) {
                                            it.joinToString(",")
                                        } else {
                                            null
                                        }
                                    },
                                startTime =
                                    filteredDates.value.first?.let {
                                        val instant = Instant.ofEpochMilli(it)
                                        instant.toString()
                                    },
                                endTime =
                                    filteredDates.value.second?.let {
                                        val instant = Instant.ofEpochMilli(it)
                                        instant.toString()
                                    },
                            ),
                    )

                // Check if there are more pages based on response size
                val hasNoMorePages = payments.isEmpty() || payments.size < PAGE_SIZE

                // Update the hasMorePages state
                _hasMorePaymentsPages.update { !hasNoMorePages }

                _paymentsShiftList.update {
                    if (nextPage) {
                        it + payments
                    } else {
                        payments
                    }
                }

                // Only increment page if we're doing pagination and we got new data
                if (nextPage && payments.isNotEmpty()) {
                    _currenPaymenttPage.update {
                        it + 1
                    }
                }
            } catch (e: Exception) {
                if (e is AvoqadoError) {
                    snackbarDelegate.showSnackbar(
                        message = e.message ?: "Algo salio mal...",
                    )
                } else {
                    Timber.e(e)
                }
                // If there's an error, assume no more pages
                _hasMorePaymentsPages.update { false }
            } finally {
                _isLoadingMore.update { false }
                _isLoading.update { false }
            }
        }
    }

    fun loadSummary() {
        viewModelScope.launch {
            try {
                _isLoading.update { true }
                val summary =
                    terminalRepository.getSummary(
                        ShiftParams(
                            pageSize = PAGE_SIZE,
                            page = 0,
                            venueId = venueInfo?.id ?: "",
                            waiterIds =
                                filteredWaiters.value.let {
                                    if (it.isNotEmpty()) {
                                        it.joinToString(",")
                                    } else {
                                        null
                                    }
                                },
                            startTime =
                                filteredDates.value.first?.let {
                                    val instant = Instant.ofEpochMilli(it)
                                    instant.toString()
                                },
                            endTime =
                                filteredDates.value.second?.let {
                                    val instant = Instant.ofEpochMilli(it)
                                    instant.toString()
                                },
                        ),
                    )

                _shiftSummary.update {
                    summary
                }
            } catch (e: Exception) {
                if (e is AvoqadoError) {
                    snackbarDelegate.showSnackbar(
                        message = e.message ?: "Algo salio mal...",
                    )
                } else {
                    Timber.e(e)
                }
            } finally {
                _isLoading.update { false }
            }
        }
    }

    fun onFilterByWaiters(waiters: List<String>) {
        _filteredWaiters.update {
            waiters
        }
        toggleWaitersSheet(false)

        _currentPage.update { 1 }
        _currenPaymenttPage.update { 1 }
        loadShiftsSummary()
        loadSummary()
        loadPaymentsSummary()
    }

    fun onFilterByDates(dates: Pair<Long?, Long?>) {
        _filteredDates.update {
            dates
        }
        toggleCreatedSheet(false)

        _currentPage.update { 1 }
        _currenPaymenttPage.update { 1 }
        loadShiftsSummary()
        loadSummary()
        loadPaymentsSummary()
    }

    fun toggleCreatedSheet(value: Boolean) {
        _showCreatedSheet.update {
            value
        }
    }

    fun toggleWaitersSheet(value: Boolean) {
        _showWaiterSheet.update {
            value
        }
    }

    fun updateTab(tabs: SummaryTabs) {
        _currentTab.update {
            tabs
        }
    }

    fun navigateBack() {
        navigationDispatcher.navigateBack()
    }
}
