package com.avoqado.pos.features.payment.presentation.paymentResult

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avoqado.pos.core.data.local.SessionManager
import com.avoqado.pos.core.domain.models.PaymentStatus
import com.avoqado.pos.core.presentation.navigation.NavigationDispatcher
import com.avoqado.pos.core.presentation.utils.toAmountMx
import com.avoqado.pos.destinations.MainDests
import com.avoqado.pos.features.payment.domain.models.PaymentInfoResult
import com.avoqado.pos.features.payment.domain.repository.PaymentRepository
import com.google.gson.Gson
import com.menta.android.core.model.Adquirer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PaymentResultViewModel(
    private val navigationDispatcher: NavigationDispatcher,
    private val paymentRepository: PaymentRepository,
    private val sessionManager: SessionManager
) : ViewModel() {
    private val _paymentResult = MutableStateFlow<PaymentResultViewState>(PaymentResultViewState())
    val paymentResult: StateFlow<PaymentResultViewState> = _paymentResult.asStateFlow()

    init {
        paymentRepository.getCachePaymentInfo()?.let {
            Log.d("PaymentResultViewModel", it.rootData)
            _paymentResult.update { state ->
                state
                    .copy(
                        tipAmount = it.tipAmount,
                        subtotalAmount = it.subtotal,
                    )
            }

            recordPayment(it)
        }
    }

    private fun recordPayment(info: PaymentInfoResult) {
        viewModelScope.launch(Dispatchers.IO) {
            val adquirer = try {
                Gson().fromJson(info.rootData, Adquirer::class.java)
            } catch (e: Exception) {
                null
            }
            val terminal = sessionManager.getTerminalInfo()
            val token =
                "${info.venueId}-${info.tableNumber}-${info.billId}-${System.currentTimeMillis()}"

            _paymentResult.update { state ->
                state
                    .copy(
                        qrCode = "https://avoqado.io/receipt?token=${token}",
                        adquirer = adquirer
                    )
            }

            paymentRepository.recordPayment(
                venueId = info.venueId,
                tableNumber = info.tableNumber,
                waiterName = info.waiterName,
                tpvId = terminal?.id ?: "",
                splitType = info.splitType?.value?:"",
                status = PaymentStatus.ACCEPTED.value,
                amount = info.subtotal.toString().toAmountMx().replace(".", "").toInt(),
                tip = info.tipAmount.toString().toAmountMx().replace(".", "").toInt(),
                billId = info.billId,
                adquirer = adquirer,
                token = token,
                paidProductsId = info.products
            )
        }
    }

    fun setPaymentResult(paymentResult: PaymentResultViewState) {
        _paymentResult.value = paymentResult
    }

    fun goToHome() {
        paymentRepository.clearCachePaymentInfo()
        navigationDispatcher.popToDestination(MainDests.Tables.route, inclusive = false)
    }

    fun newPayment() {
        paymentRepository.clearCachePaymentInfo()
        navigationDispatcher.popToDestination(MainDests.TableDetail.route, inclusive = false)
    }
}