package com.avoqado.pos.features.payment.presentation.paymentResult

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avoqado.pos.AvoqadoApp
import com.avoqado.pos.core.data.network.AppConfig
import com.avoqado.pos.core.domain.models.PaymentStatus
import com.avoqado.pos.core.domain.models.SplitType
import com.avoqado.pos.core.domain.models.TerminalInfo
import com.avoqado.pos.core.domain.repositories.TerminalRepository
import com.avoqado.pos.core.presentation.model.Product
import com.avoqado.pos.core.presentation.navigation.NavigationDispatcher
import com.avoqado.pos.core.presentation.utils.toAmountMx
import com.avoqado.pos.features.management.domain.ManagementRepository
import com.avoqado.pos.features.management.presentation.navigation.ManagementDests
import com.avoqado.pos.features.payment.domain.models.PaymentInfoResult
import com.avoqado.pos.features.payment.domain.repository.PaymentRepository
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

class PaymentResultViewModel(
    private val navigationDispatcher: NavigationDispatcher,
    private val paymentRepository: PaymentRepository,
    private val terminalRepository: TerminalRepository,
    private val managementRepository: ManagementRepository,
) : ViewModel() {
    private val _paymentResult = MutableStateFlow<PaymentResultViewState>(PaymentResultViewState())
    val paymentResult: StateFlow<PaymentResultViewState> = _paymentResult.asStateFlow()
    val venue = AvoqadoApp.sessionManager.getVenueInfo()
    val tableInfo = managementRepository.getCachedTable()

    init {
        paymentRepository.getCachePaymentInfo()?.let {
            Timber.d(it.rootData)
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
            val adquirer =
                try {
//                    Gson().fromJson(info.rootData, Adquirer::class.java)
                    null
                } catch (e: Exception) {
                    null
                }

            when (info.splitType) {
                SplitType.PERPRODUCT ->
                    _paymentResult.update { state ->
                        state.copy(
                            paidProducts =
                                tableInfo
                                    ?.products
                                    ?.filter { product -> product.id in info.products }
                                    ?.map { product ->
                                        Product(
                                            id = product.id,
                                            name = product.name,
                                            price = product.price,
                                            quantity = product.quantity,
                                            totalPrice = product.price * product.quantity,
                                        )
                                    } ?: emptyList(),
                        )
                    }

                SplitType.EQUALPARTS ->
                    _paymentResult.update { state ->
                        state.copy(
                            paidProducts =
                                listOf(
                                    Product(
                                        id = "",
                                        name = "${info.splitSelectedPartySize} de ${info.splitPartySize}",
                                        price = info.subtotal,
                                        quantity = 1.0,
                                        totalPrice = info.subtotal,
                                    ),
                                ),
                        )
                    }

                SplitType.CUSTOMAMOUNT ->
                    _paymentResult.update { state ->
                        state.copy(
                            paidProducts =
                                listOf(
                                    Product(
                                        id = "",
                                        name = "Monto personalizado",
                                        price = info.subtotal,
                                        quantity = 1.0,
                                        totalPrice = info.subtotal,
                                    ),
                                ),
                        )
                    }

                SplitType.FULLPAYMENT ->
                    _paymentResult.update { state ->
                        state.copy(
                            paidProducts =
                                listOf(
                                    Product(
                                        id = "",
                                        name = "Pago completo",
                                        price = info.subtotal,
                                        quantity = 1.0,
                                        totalPrice = info.subtotal,
                                    ),
                                ),
                        )
                    }

                null -> {}
            }

            val terminal =
                try {
                    terminalRepository.getTerminalId(AvoqadoApp.terminalSerialCode)
                } catch (e: Exception) {
                    Timber.e(e)
                    TerminalInfo(
                        serialCode = "",
                        id = "",
                    )
                }
            // Generate token based on whether this is a quick payment or a regular payment
            val token = if (info.splitType == SplitType.FULLPAYMENT && info.tableNumber.isEmpty()) {
                // Fast payment format for quick payments
                "${info.venueId}---${System.currentTimeMillis()}"
            } else {
                // Regular payment format for table-based payments
                "${info.venueId}-${info.tableNumber}-${info.billId}-${System.currentTimeMillis()}"
            }

            val receiptUrl = "${AppConfig.getWebFrontendUrl()}/receipt?token=$token${if (info.reviewRating == com.avoqado.pos.features.payment.presentation.review.ReviewRating.EXCELLENT) "&avoidReview=false" else "&avoidReview=true"}"

            _paymentResult.update { state ->
                state
                    .copy(
                        qrCode = receiptUrl,
                        adquirer = adquirer,
                        terminalSerialCode = terminal.serialCode,
                    )
            }

            // Determine if this is a quick payment (no table number) or regular payment
            val isQuickPayment = info.splitType == SplitType.FULLPAYMENT && info.tableNumber.isEmpty()
            
            _paymentResult.update { state ->
                state.copy(
                    isQuickPayment = isQuickPayment
                )
            }
            
            if (isQuickPayment) {
                // Use fast payment endpoint for quick payments
                info.splitType?.let {
                    paymentRepository.recordFastPayment(
                        venueId = info.venueId,
                        waiterName = info.waiterName,
                        tpvId = terminal.id,
                        splitType = it.value,
                        status = PaymentStatus.ACCEPTED.value,
                        amount =
                        info.subtotal
                            .toString()
                            .toAmountMx()
                            .replace(".", "")
                            .toInt(),
                        tip =
                        info.tipAmount
                            .toString()
                            .toAmountMx()
                            .replace(".", "")
                            .toInt(),
                        adquirer = adquirer,
                        token = token,
                        paidProductsId = info.products,
                        reviewRating = info.reviewRating,
                    )
                }
            } else {
                // Use regular payment endpoint for table-based payments
                paymentRepository.recordPayment(
                    venueId = info.venueId,
                    tableNumber = info.tableNumber,
                    waiterName = info.waiterName,
                    tpvId = terminal.id,
                    splitType = info.splitType?.value ?: "",
                    status = PaymentStatus.ACCEPTED.value,
                    amount =
                        info.subtotal
                            .toString()
                            .toAmountMx()
                            .replace(".", "")
                            .toInt(),
                    tip =
                        info.tipAmount
                            .toString()
                            .toAmountMx()
                            .replace(".", "")
                            .toInt(),
                    billId = info.billId,
                    adquirer = adquirer,
                    token = token,
                    paidProductsId = info.products,
                    reviewRating = info.reviewRating,
                )
            }
        }
    }

    fun setPaymentResult(paymentResult: PaymentResultViewState) {
        _paymentResult.value = paymentResult
    }

    fun goToHome() {
        paymentRepository.clearCachePaymentInfo()
        navigationDispatcher.popToDestination(ManagementDests.Home.route, inclusive = false)
    }

    fun newPayment() {
        paymentRepository.clearCachePaymentInfo()
        if (tableInfo != null) {
            navigationDispatcher.popToDestination(ManagementDests.TableDetail.route, inclusive = false)
        } else {
            navigationDispatcher.popToDestination(ManagementDests.Home.route, inclusive = false)
        }
    }
}
