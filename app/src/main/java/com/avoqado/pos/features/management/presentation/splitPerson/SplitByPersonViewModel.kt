package com.avoqado.pos.features.management.presentation.splitPerson

import androidx.lifecycle.ViewModel
import com.avoqado.pos.core.domain.models.SplitType
import com.avoqado.pos.core.presentation.navigation.NavigationArg
import com.avoqado.pos.core.presentation.navigation.NavigationDispatcher
import com.avoqado.pos.core.presentation.utils.toAmountMx
import com.avoqado.pos.features.management.domain.ManagementRepository
import com.avoqado.pos.features.payment.domain.repository.PaymentRepository
import com.avoqado.pos.features.payment.presentation.navigation.PaymentDests
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SplitByPersonViewModel(
    private val navigationDispatcher: NavigationDispatcher,
    private val managementRepository: ManagementRepository,
    private val paymentRepository: PaymentRepository,
    private val sessionManager: com.avoqado.pos.core.data.local.SessionManager,
) : ViewModel() {
    private val _state = MutableStateFlow<SplitByPersonViewState>(SplitByPersonViewState())
    val state: StateFlow<SplitByPersonViewState> = _state.asStateFlow()

    private var waiterName: String = ""

    init {
        managementRepository.getCachedTable()?.let { cache ->
            waiterName = cache.waiterName ?: ""
            _state.update {
                it.copy(
                    splitPartyPaidSize = cache.paymentOverview?.equalPartyPaidSize ?: 0,
                    splitPartySize = cache.paymentOverview?.equalPartySize ?: 0,
                    totalPendingAmount = cache.totalPending.toString().toAmountMx(),
                )
            }
        }
    }

    fun updateSplitPartySize(increase: Boolean) {
        if (increase) {
            _state.update {
                it.copy(
                    splitPartySize = (it.splitPartySize + 1).coerceAtMost(99),
                )
            }
        } else {
            _state.update {
                it.copy(
                    splitPartySize = (it.splitPartySize - 1).coerceAtLeast(0),
                )
            }
        }
    }

    fun onItemSelected(index: Int) {
        if (_state.value.splitPartySelected.contains(index)) {
            _state.update {
                it.copy(
                    splitPartySelected = it.splitPartySelected - index,
                )
            }
        } else {
            _state.update {
                it.copy(
                    splitPartySelected = it.splitPartySelected + index,
                )
            }
        }
    }

    fun navigateBack() {
        navigationDispatcher.navigateBack()
    }

    fun navigateToPayment() {
        paymentRepository.getCachePaymentInfo()?.let { info ->
            paymentRepository.setCachePaymentInfo(
                info.copy(
                    splitPartySize = _state.value.splitPartySize,
                    splitSelectedPartySize = _state.value.splitPartySelected.size,
                ),
            )
        }

        navigationDispatcher.navigateWithArgs(
            PaymentDests.LeaveReview,
            NavigationArg.StringArg(
                PaymentDests.LeaveReview.ARG_SUBTOTAL,
                _state.value.totalSelectedAmount,
            ),
            NavigationArg.StringArg(
                PaymentDests.LeaveReview.ARG_WAITER,
                waiterName,
            ),
            NavigationArg.StringArg(
                PaymentDests.LeaveReview.ARG_SPLIT_TYPE,
                SplitType.EQUALPARTS.value,
            ),
            NavigationArg.StringArg(
                PaymentDests.LeaveReview.ARG_VENUE_NAME,
                sessionManager.getVenueInfo()?.name ?: "",
            ),
        )
    }
}
