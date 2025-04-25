package com.avoqado.pos.features.payment.presentation.quickPayment

import androidx.lifecycle.ViewModel
import com.avoqado.pos.core.data.local.SessionManager
import com.avoqado.pos.core.domain.models.SplitType
import com.avoqado.pos.core.presentation.navigation.NavigationArg
import com.avoqado.pos.core.presentation.navigation.NavigationDispatcher
import com.avoqado.pos.features.payment.domain.models.PaymentInfoResult
import com.avoqado.pos.features.payment.domain.repository.PaymentRepository
import com.avoqado.pos.features.payment.presentation.navigation.PaymentDests
import java.time.LocalDateTime

class QuickPaymentViewModel(
    private val navigationDispatcher: NavigationDispatcher,
    private val sessionManager: SessionManager,
    private val paymentRepository: PaymentRepository,
) : ViewModel() {
    val currentUser = sessionManager.getAvoqadoSession()

    fun onBack() {
        navigationDispatcher.navigateBack()
    }

    fun submitAmount(amount: Double) {
        paymentRepository.setCachePaymentInfo(
            PaymentInfoResult(
                paymentId = "",
                tipAmount = 0.0,
                subtotal = amount,
                rootData = "",
                date = LocalDateTime.now(),
                waiterName = currentUser?.name ?: "",
                tableNumber = "",
                venueId = currentUser?.venueId ?: "",
                splitType = SplitType.FULLPAYMENT,
                billId = "",
            ),
        )
        navigationDispatcher.navigateBack()
        navigationDispatcher.navigateWithArgs(
            PaymentDests.InputTip,
            NavigationArg.StringArg(
                PaymentDests.InputTip.ARG_SUBTOTAL,
                amount.toString(),
            ),
            NavigationArg.StringArg(
                PaymentDests.InputTip.ARG_WAITER,
                currentUser?.name ?: "",
            ),
            NavigationArg.StringArg(
                PaymentDests.InputTip.ARG_SPLIT_TYPE,
                SplitType.EQUALPARTS.value,
            ),
        )
    }
}
