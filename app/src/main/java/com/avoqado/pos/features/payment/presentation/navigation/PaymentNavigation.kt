package com.avoqado.pos.features.payment.presentation.navigation

import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraphBuilder
import com.avoqado.pos.AvoqadoApp
import com.avoqado.pos.OperationFlowHolder
import com.avoqado.pos.core.data.local.SessionManager
import com.avoqado.pos.core.domain.models.SplitType
import com.avoqado.pos.core.domain.usecase.ValidateAmountUseCase
import com.avoqado.pos.core.presentation.navigation.NavigationDispatcher
import com.avoqado.pos.destinations.MainDests
import com.avoqado.pos.features.payment.presentation.inputTipAmount.InputTipScreen
import com.avoqado.pos.features.payment.presentation.inputTipAmount.InputTipViewModel
import com.avoqado.pos.features.payment.presentation.paymentResult.PaymentResultScreen
import com.avoqado.pos.features.payment.presentation.paymentResult.PaymentResultViewModel
import com.avoqado.pos.router.composableHolder
import com.menta.android.printer.i9100.core.DevicePrintImpl

fun NavGraphBuilder.paymentNavigation(navigationDispatcher: NavigationDispatcher, sessionManager: SessionManager) {
    composableHolder(MainDests.InputTip) {
        val subtotal = it.arguments?.getString(MainDests.InputTip.ARG_SUBTOTAL) ?: "0.00"
        val waiterName = it.arguments?.getString(MainDests.InputTip.ARG_WAITER) ?: ""
        val splitType = it.arguments?.getString(MainDests.InputTip.ARG_SPLIT_TYPE) ?: ""
        val inputTipViewModel = remember {
            InputTipViewModel(
                subtotal = subtotal,
                waiterName = waiterName,
                splitType = SplitType.valueOf(splitType),
                navigationDispatcher = navigationDispatcher,
                validateAmountUseCase = ValidateAmountUseCase()
            )
        }

        InputTipScreen(inputTipViewModel = inputTipViewModel)
    }

    composableHolder(MainDests.PaymentResult) {
        val context = LocalContext.current
        val paymentResultViewModel = remember {
            PaymentResultViewModel(
                navigationDispatcher = navigationDispatcher,
                paymentRepository = AvoqadoApp.paymentRepository,
                sessionManager = sessionManager
            )
        }
        PaymentResultScreen(
            paymentResultViewModel,
            DevicePrintImpl(context)
        )
    }
}