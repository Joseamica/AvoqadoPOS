package com.avoqado.pos.features.payment.presentation.quickPayment

import androidx.lifecycle.ViewModel
import com.avoqado.pos.core.data.local.SessionManager
import com.avoqado.pos.core.domain.models.SplitType
import com.avoqado.pos.core.presentation.navigation.NavigationArg
import com.avoqado.pos.core.presentation.navigation.NavigationDispatcher
import com.avoqado.pos.features.payment.presentation.navigation.PaymentDests

class QuickPaymentViewModel(
    private val navigationDispatcher: NavigationDispatcher,
    private val sessionManager: SessionManager
) : ViewModel() {

    val currentUser = sessionManager.getAvoqadoSession()

    fun onBack() {
        navigationDispatcher.navigateBack()
    }

    fun submitAmount(amount: Double) {
        navigationDispatcher.navigateBack()
        navigationDispatcher.navigateWithArgs(
            PaymentDests.InputTip,
            NavigationArg.StringArg(
                PaymentDests.InputTip.ARG_SUBTOTAL,
                amount.toString()
            ),
            NavigationArg.StringArg(
                PaymentDests.InputTip.ARG_WAITER,
                currentUser?.name ?: ""
            ),
            NavigationArg.StringArg(
                PaymentDests.InputTip.ARG_SPLIT_TYPE,
                SplitType.EQUALPARTS.value
            ),
        )
    }
}