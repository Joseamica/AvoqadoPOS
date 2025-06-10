package com.avoqado.pos.features.payment.presentation.inputTipAmount


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avoqado.pos.core.data.local.SessionManager
import com.avoqado.pos.core.domain.models.SplitType
import com.avoqado.pos.core.domain.usecase.ValidateAmountUseCase
import com.avoqado.pos.core.presentation.delegates.SnackbarDelegate
import com.avoqado.pos.core.presentation.navigation.NavigationDispatcher
import com.avoqado.pos.core.presentation.utils.toAmountMx
import com.avoqado.pos.features.payment.domain.repository.PaymentRepository
import com.avoqado.pos.features.payment.presentation.navigation.PaymentDests
import com.example.content_core_service.ContentService
import com.example.content_core_service.transaction_service.models.TransactionStatusList
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDateTime

class InputTipViewModel(
    val subtotal: String,
    val waiterName: String,
    val splitType: SplitType,
    private val validateAmountUseCase: ValidateAmountUseCase,
    private val navigationDispatcher: NavigationDispatcher,
    private val sessionManager: SessionManager,
    private val contentService: ContentService,
    private val snackbarDelegate: SnackbarDelegate,
    private val paymentRepository: PaymentRepository
) : ViewModel() {
    private val _showCustomAmount = MutableStateFlow(false)
    val showCustomAmount: StateFlow<Boolean> = _showCustomAmount

    private val _paymentDeclined = MutableStateFlow(false)
    val paymentDeclined: StateFlow<Boolean> = _paymentDeclined

    // Default tip percentages (12%, 15%, 18%) - will be overridden with venue settings if available
    private val _tipPercentages = MutableStateFlow(listOf(0.12f, 0.15f, 0.18f))
    val tipPercentages = _tipPercentages.asStateFlow()

    // Tip percentage labels for UI display (to match percentages)
    private val _tipPercentageLabels = MutableStateFlow(listOf("12%", "15%", "18%"))
    val tipPercentageLabels = _tipPercentageLabels.asStateFlow()

    init {
        // Load tip percentages from the venue stored in SessionManager
        loadVenueTipPercentages()
    }

    /**
     * Loads tip percentages from the venue data already stored in SessionManager
     * This avoids making additional network calls since venue data is fetched during app initialization
     */
    private fun loadVenueTipPercentages() {
        try {
            // Get venue info from session manager (already fetched during app initialization)
            val venue = sessionManager.getVenueInfo()

            venue?.let {
                // Parse tip percentages from venue data with fallback values
                val percentage1 = it.tipPercentage1?.toFloatOrNull() ?: 0.12f
                val percentage2 = it.tipPercentage2?.toFloatOrNull() ?: 0.15f
                val percentage3 = it.tipPercentage3?.toFloatOrNull() ?: 0.18f

                val percentages = listOf(percentage1, percentage2, percentage3)
                _tipPercentages.value = percentages

                // Update the labels too
                _tipPercentageLabels.value = percentages.map { percentage ->
                    "${(percentage * 100).toInt()}%"
                }

                Timber.d("Loaded tip percentages from venue: $percentages")
            }
        } catch (e: Exception) {
            // If there's an error, keep using the default values
            Timber.e("Error loading tip percentages from venue", e)
        }
    }

    fun showCustomAmountKeyboard() {
        _showCustomAmount.update {
            true
        }
    }

    fun hideCustomAmountKeyboard() {
        _showCustomAmount.update {
            false
        }
    }

    fun navigateBack() {
        navigationDispatcher.navigateBack()
    }

    fun startPayment(tip: Double) {
        viewModelScope.launch {
            val amount = subtotal.toAmountMx().toDouble() * 100
            val transaction = contentService.register(amount.toInt())
            val result = transaction.transaction?.id?.let {
                contentService.startTransaction(it)
            }

            if (result != null) {
                result.transaction?.let { transaction ->
                    paymentRepository.getCachePaymentInfo()?.let { info ->
                        paymentRepository.setCachePaymentInfo(
                            paymentInfoResult =
                                info.copy(
                                    paymentId = transaction.id ?: "",
                                    tipAmount = tip,
                                    subtotal = (transaction.amount.toDouble() / 100),
                                    date = LocalDateTime.now(),
                                    rootData = Gson().toJson(transaction),
                                    splitType = splitType,
                                    waiterName = waiterName,
                                ),
                        )
                    }

                    navigateBack()
                    when (transaction.status) {
                        TransactionStatusList.Pending -> {
                            snackbarDelegate.showSnackbar(message = "Transaccion iniciada")
                            navigationDispatcher.navigateTo(PaymentDests.PaymentResult)
                        }
                        TransactionStatusList.Failed, TransactionStatusList.Cancelled, TransactionStatusList.Denied -> {
                            _paymentDeclined.update {
                                true
                            }
                        }

                        TransactionStatusList.Approved, TransactionStatusList.Refunded -> {
                            navigationDispatcher.navigateTo(PaymentDests.PaymentResult)
                        }
                    }
                }
            } else {
                snackbarDelegate.showSnackbar(message = "Error al iniciar transaccion")
                navigateBack()
            }
        }
    }
}
