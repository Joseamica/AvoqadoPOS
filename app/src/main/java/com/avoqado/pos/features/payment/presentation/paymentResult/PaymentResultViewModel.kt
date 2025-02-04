package com.avoqado.pos.features.payment.presentation.paymentResult

import androidx.lifecycle.ViewModel
import com.avoqado.pos.core.presentation.navigation.NavigationDispatcher
import com.avoqado.pos.features.payment.domain.repository.PaymentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PaymentResultViewModel(
    private val navigationDispatcher: NavigationDispatcher,
    private val paymentRepository: PaymentRepository
): ViewModel(){
    private val _paymentResult = MutableStateFlow<PaymentResultViewState>(PaymentResultViewState())
    val paymentResult: StateFlow<PaymentResultViewState> = _paymentResult.asStateFlow()

    init {
        paymentRepository.getCachePaymentInfo()?.let {
            _paymentResult.update {
                it.copy(
                    tipAmount = it.tipAmount,
                    subtotalAmount = it.subtotalAmount,
                )
            }
        }
    }

    fun setPaymentResult(paymentResult: PaymentResultViewState){
        _paymentResult.value = paymentResult
    }

    fun goToHome(){
        paymentRepository.clearCachePaymentInfo()
        navigationDispatcher.navigateBack()
    }

    fun newPayment(){
        paymentRepository.clearCachePaymentInfo()
        navigationDispatcher.navigateBack()
    }
}