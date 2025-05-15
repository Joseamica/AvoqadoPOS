package com.avoqado.pos.features.payment.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import com.avoqado.pos.AvoqadoApp
import com.avoqado.pos.core.domain.models.PaymentShift
import com.avoqado.pos.core.domain.models.SplitType
import com.avoqado.pos.core.domain.usecase.ValidateAmountUseCase
import com.avoqado.pos.core.presentation.delegates.SnackbarDelegate
import com.avoqado.pos.core.presentation.navigation.NavigationDispatcher
import com.avoqado.pos.core.presentation.router.bottomSheetHolder
import com.avoqado.pos.core.presentation.router.composableHolder
import com.avoqado.pos.features.payment.presentation.inputTipAmount.InputTipScreen
import com.avoqado.pos.features.payment.presentation.inputTipAmount.InputTipViewModel
import com.avoqado.pos.features.payment.presentation.paymentResult.PaymentResultScreen
import com.avoqado.pos.features.payment.presentation.paymentResult.PaymentResultViewModel
import com.avoqado.pos.features.payment.presentation.quickPayment.QuickPaymentSheet
import com.avoqado.pos.features.payment.presentation.quickPayment.QuickPaymentViewModel
import com.avoqado.pos.features.payment.presentation.review.LeaveReviewScreen
import com.avoqado.pos.features.payment.presentation.review.LeaveReviewViewModel
import com.avoqado.pos.features.payment.presentation.transactions.SummaryTabs
import com.avoqado.pos.features.payment.presentation.transactions.TransactionSummaryViewModel
import com.avoqado.pos.features.payment.presentation.transactions.TransactionsSummaryScreen
import com.avoqado.pos.features.payment.presentation.transactions.components.PaymentDetailScreen
import com.menta.android.core.viewmodel.TrxData

fun NavGraphBuilder.paymentNavigation(
    navigationDispatcher: NavigationDispatcher,
    snackbarDelegate: SnackbarDelegate,
    trxData: TrxData,
) {
    composableHolder(PaymentDests.LeaveReview) {
        val subtotal = it.arguments?.getString(PaymentDests.LeaveReview.ARG_SUBTOTAL) ?: "0.00"
        val waiterName = it.arguments?.getString(PaymentDests.LeaveReview.ARG_WAITER) ?: ""
        val splitType = it.arguments?.getString(PaymentDests.LeaveReview.ARG_SPLIT_TYPE) ?: ""
        val venueName = it.arguments?.getString(PaymentDests.LeaveReview.ARG_VENUE_NAME) ?: ""
        val leaveReviewViewModel =
            remember {
                LeaveReviewViewModel(
                    subtotal = subtotal,
                    waiterName = waiterName,
                    splitType = splitType,
                    venueName = venueName,
                    navigationDispatcher = navigationDispatcher
                )
            }

        LeaveReviewScreen(leaveReviewViewModel = leaveReviewViewModel)
    }
    
    composableHolder(PaymentDests.InputTip) {
        val subtotal = it.arguments?.getString(PaymentDests.InputTip.ARG_SUBTOTAL) ?: "0.00"
        val waiterName = it.arguments?.getString(PaymentDests.InputTip.ARG_WAITER) ?: ""
        val splitType = it.arguments?.getString(PaymentDests.InputTip.ARG_SPLIT_TYPE) ?: ""
        val inputTipViewModel =
            remember {
                InputTipViewModel(
                    subtotal = subtotal,
                    waiterName = waiterName,
                    splitType = SplitType.valueOf(splitType),
                    navigationDispatcher = navigationDispatcher,
                    validateAmountUseCase = ValidateAmountUseCase(),
                    sessionManager = AvoqadoApp.sessionManager
                )
            }

        InputTipScreen(inputTipViewModel = inputTipViewModel)
    }

    composableHolder(PaymentDests.PaymentResult) {
        val paymentResultViewModel =
            remember {
                PaymentResultViewModel(
                    navigationDispatcher = navigationDispatcher,
                    paymentRepository = AvoqadoApp.paymentRepository,
                    terminalRepository = AvoqadoApp.terminalRepository,
                    managementRepository = AvoqadoApp.managementRepository,
                )
            }
        PaymentResultScreen(
            paymentResultViewModel,
        )
    }

    composableHolder(PaymentDests.TransactionsSummary) {
        val tab = it.arguments?.getString(PaymentDests.TransactionsSummary.ARG_TAB)
        val summaryViewModel =
            remember {
                TransactionSummaryViewModel(
                    sessionManager = AvoqadoApp.sessionManager,
                    navigationDispatcher = navigationDispatcher,
                    snackbarDelegate = snackbarDelegate,
                    terminalRepository = AvoqadoApp.terminalRepository,
                    initialTab = tab?.let { name -> SummaryTabs.valueOf(name) } ?: SummaryTabs.RESUMEN,
                )
            }

        TransactionsSummaryScreen(
            viewModel = summaryViewModel,
        )
    }

    bottomSheetHolder(PaymentDests.QuickPayment) {
        val viewModel =
            remember {
                QuickPaymentViewModel(
                    navigationDispatcher = navigationDispatcher,
                    sessionManager = AvoqadoApp.sessionManager,
                    paymentRepository = AvoqadoApp.paymentRepository,
                )
            }

        QuickPaymentSheet(
            viewModel = viewModel,
        )
    }
    
    composableHolder(PaymentDests.PaymentDetail) {
        val paymentId = it.arguments?.getString(PaymentDests.PaymentDetail.ARG_PAYMENT_ID) ?: ""
        val paymentState = remember { mutableStateOf<PaymentShift?>(null) }
        
        // Launch a coroutine effect to fetch the payment data
        LaunchedEffect(paymentId) {
            // Find the payment in the terminal repository
            val payment = AvoqadoApp.terminalRepository.getPaymentById(paymentId)
            paymentState.value = payment
        }
        
        // Show loading state while payment is being fetched
        if (paymentState.value == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            // Show payment details if payment was found
            paymentState.value?.let { payment ->
                PaymentDetailScreen(
                    payment = payment,
                    onBackClick = { navigationDispatcher.navigateBack() }
                )
            } ?: run {
                // Handle case where payment is not found
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Payment not found")
                }
            }
        }
    }
}
