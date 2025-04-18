package com.avoqado.pos.features.payment.presentation.navigation

import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraphBuilder
import com.avoqado.pos.AvoqadoApp
import com.avoqado.pos.OperationFlowHolder
import com.avoqado.pos.core.data.local.SessionManager
import com.avoqado.pos.core.domain.models.SplitType
import com.avoqado.pos.core.domain.usecase.ValidateAmountUseCase
import com.avoqado.pos.core.presentation.delegates.SnackbarDelegate
import com.avoqado.pos.core.presentation.navigation.NavigationDispatcher
import com.avoqado.pos.core.presentation.destinations.MainDests
import com.avoqado.pos.features.payment.presentation.inputTipAmount.InputTipScreen
import com.avoqado.pos.features.payment.presentation.inputTipAmount.InputTipViewModel
import com.avoqado.pos.features.payment.presentation.paymentResult.PaymentResultScreen
import com.avoqado.pos.features.payment.presentation.paymentResult.PaymentResultViewModel
import com.avoqado.pos.features.payment.presentation.quickPayment.QuickPaymentSheet
import com.avoqado.pos.features.payment.presentation.quickPayment.QuickPaymentViewModel
import com.avoqado.pos.features.payment.presentation.transactions.TransactionSummaryViewModel
import com.avoqado.pos.features.payment.presentation.transactions.TransactionsSummaryScreen
import com.avoqado.pos.core.presentation.router.bottomSheetHolder
import com.avoqado.pos.core.presentation.router.composableHolder
import com.avoqado.pos.features.payment.presentation.transactions.SummaryTabs
import com.menta.android.core.viewmodel.TrxData
import com.menta.android.printer.i9100.core.DevicePrintImpl

fun NavGraphBuilder.paymentNavigation(
    navigationDispatcher: NavigationDispatcher,
    snackbarDelegate: SnackbarDelegate,
    trxData: TrxData
) {
    composableHolder(PaymentDests.InputTip) {
        val subtotal = it.arguments?.getString(PaymentDests.InputTip.ARG_SUBTOTAL) ?: "0.00"
        val waiterName = it.arguments?.getString(PaymentDests.InputTip.ARG_WAITER) ?: ""
        val splitType = it.arguments?.getString(PaymentDests.InputTip.ARG_SPLIT_TYPE) ?: ""
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

    composableHolder(PaymentDests.PaymentResult) {
        val paymentResultViewModel = remember {
            PaymentResultViewModel(
                navigationDispatcher = navigationDispatcher,
                paymentRepository = AvoqadoApp.paymentRepository,
                terminalRepository = AvoqadoApp.terminalRepository,
                managementRepository = AvoqadoApp.managementRepository
            )
        }
        PaymentResultScreen(
            paymentResultViewModel
        )
    }

    composableHolder(PaymentDests.TransactionsSummary) {
        val tab = it.arguments?.getString(PaymentDests.TransactionsSummary.ARG_TAB)
        val summaryViewModel = remember {
            TransactionSummaryViewModel(
                sessionManager = AvoqadoApp.sessionManager,
                navigationDispatcher = navigationDispatcher,
                snackbarDelegate = snackbarDelegate,
                terminalRepository = AvoqadoApp.terminalRepository,
                initialTab = tab?.let { name -> SummaryTabs.valueOf(name) } ?: SummaryTabs.RESUMEN
            )
        }

        TransactionsSummaryScreen(
            viewModel = summaryViewModel,
        )
    }

    bottomSheetHolder(PaymentDests.QuickPayment) {
        val viewModel = remember {
            QuickPaymentViewModel(
                navigationDispatcher = navigationDispatcher,
                sessionManager = AvoqadoApp.sessionManager,
                paymentRepository = AvoqadoApp.paymentRepository
            )
        }

        QuickPaymentSheet(
            viewModel = viewModel
        )
    }
}