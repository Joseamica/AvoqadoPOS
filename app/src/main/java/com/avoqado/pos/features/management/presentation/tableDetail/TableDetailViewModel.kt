package com.avoqado.pos.features.management.presentation.tableDetail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avoqado.pos.AvoqadoApp
import com.avoqado.pos.core.presentation.delegates.SnackbarDelegate
import com.avoqado.pos.core.presentation.navigation.NavigationArg
import com.avoqado.pos.core.presentation.navigation.NavigationDispatcher
import com.avoqado.pos.core.domain.models.SplitType
import com.avoqado.pos.core.presentation.model.Product
import com.avoqado.pos.features.management.domain.ManagementRepository
import com.avoqado.pos.features.management.domain.usecases.ListenTableAction
import com.avoqado.pos.features.management.domain.usecases.ListenTableEventsUseCase
import com.avoqado.pos.features.management.presentation.navigation.ManagementDests
import com.avoqado.pos.features.management.presentation.tableDetail.model.Payment
import com.avoqado.pos.features.management.presentation.tableDetail.model.TableDetailView
import com.avoqado.pos.features.management.presentation.tableDetail.model.toDomain
import com.avoqado.pos.features.payment.domain.models.PaymentInfoResult
import com.avoqado.pos.features.payment.presentation.navigation.PaymentDests
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.time.LocalDateTime

class TableDetailViewModel(
    private val tableNumber: String = "",
    private val venueId: String = "",
    private val navigationDispatcher: NavigationDispatcher,
    private val snackbarDelegate: SnackbarDelegate,
    private val managementRepository: ManagementRepository,
    private val listenTableEventsUseCase: ListenTableEventsUseCase
) : ViewModel() {
    val venue = AvoqadoApp.sessionManager.getVenueInfo()
    val currentShift = AvoqadoApp.sessionManager.getShift()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _tableDetail = MutableStateFlow<TableDetailView>(TableDetailView())
    val tableDetail: StateFlow<TableDetailView> = _tableDetail.asStateFlow()

    private val _showPaymentPicker = MutableStateFlow(false)
    val showPaymentPicker: StateFlow<Boolean> = _showPaymentPicker.asStateFlow()

    fun navigateBack() {
        navigationDispatcher.navigateBack()
    }

    fun showPaymentPicker() {
        _showPaymentPicker.value = true
    }

    fun hidePaymentPicker() {
        _showPaymentPicker.value = false
    }

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun onPullToRefreshTrigger() {
        _isRefreshing.update { true }
        viewModelScope.launch {
            _isRefreshing.update { false }
            fetchTableDetail()
        }
    }

    fun startListeningUpdates() {
        viewModelScope.launch(Dispatchers.IO) {
            listenTableEventsUseCase.invoke(
                ListenTableAction.Connect(
                    venueId = venueId,
                    tableId = tableNumber
                )
            ).collectLatest { result ->
                Log.d("AvoqadoSocket", result.toString())
                _tableDetail.update { state ->
                    state.copy(
                        paymentsDone = state.paymentsDone.toMutableList().apply {
                            add(
                                Payment(
                                    amount = result.amount,
                                    products = emptyList(),
                                    splitType = result.splitType.value,
                                    equalPartsPartySize = "",
                                    equalPartsPayedFor = ""
                                )
                            )
                        }
                    )
                }
            }
        }
    }

    fun stopListeningUpdates() {
        viewModelScope.launch(Dispatchers.IO) {
            listenTableEventsUseCase.invoke(
                ListenTableAction.Disconnect
            )
        }
    }

    fun fetchTableDetail() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            try {
                managementRepository.getDetailedBill(
                    venueId = venueId,
                    billId = tableNumber
                ).let { billDetail ->
                    _tableDetail.update {
                        it.copy(
                            name = billDetail.name,
                            tableId = billDetail.tableId,
                            billId = billDetail.billId,
                            totalAmount = billDetail.totalAmount,
                            waiterName = billDetail.waiterName ?: "",
                            products = billDetail.products.map { item ->
                                Product(
                                    id = item.id,
                                    name = item.name,
                                    price = item.price,
                                    quantity = item.quantity,
                                    totalPrice = item.price * item.quantity
                                )
                            },
                            paymentsDone = billDetail.paymentsDone.map { payment ->
                                Payment(
                                    amount = payment.amount,
                                    products = payment.products,
                                    splitType = payment.splitType,
                                    equalPartsPayedFor = payment.equalPartsPayedFor,
                                    equalPartsPartySize = payment.equalPartsPartySize
                                )
                            },
                            currentSplitType = billDetail.paymentsDone.lastOrNull()?.splitType?.let {
                                SplitType.valueOf(it)
                            }
                        )
                    }

                    managementRepository.setTableCache(
                        _tableDetail.value.toDomain()
                    )
                }
//                val result = AvoqadoAPI.apiService.getVenueTableDetail(
//                    venueId = venueId,
//                    tableNumber = tableNumber
//                )
//
//                _tableDetail.value = TableDetail(
//                    tableId = tableNumber,
//                    name = "Mesa $tableNumber",
//                )
//
//                val billDetail = AvoqadoAPI.apiService.getTableBill(
//                    venueId = venueId,
//                    billId = result.table?.bill?.id ?: ""
//                )
//
//                _tableDetail.update {
//                    _tableDetail.value.copy(
//                        billId = result.table?.bill?.id ?: "",
//                        totalAmount = billDetail.total.toString().toAmountMXDouble(),
//                        waiterName = billDetail.waiterName ?: "",
//                        products = billDetail.products.groupBy { it.id }.map { pair ->
//                            val item = pair.value.first()
//                            Product(
//                                id = item.id,
//                                name = item.name,
//                                price = pair.value.maxOf { it.price.toAmountMXDouble() },
//                                quantity = pair.value.sumOf { it.quantity },
//                                totalPrice = pair.value.sumOf { it.price.toAmountMXDouble() }
//                            )
//                        },
//                        paymentsDone = billDetail.payments?.map { payment -> Payment(
//                                amount = payment.amount.toAmountMXDouble(),
//                                products = payment.products?.map { it.id } ?: emptyList(),
//                                splitType = payment.splitType,
//                                equalPartsPayedFor = payment.equalPartsPayedFor,
//                                equalPartsPartySize = payment.equalPartsPartySize
//                            )
//                        } ?: emptyList(),
//                        currentSplitType = billDetail.payments?.lastOrNull()?.splitType?.let {
//                            SplitType.valueOf(it)
//                        }
//                    )
//                }
//

            } catch (e: Exception) {
                Log.i("TableDetailViewModel", "Error fetching table detail", e)
                if (e is HttpException) {
                    snackbarDelegate.showSnackbar(
                        message = e.message() ?: "Ocurrio un error!"
                    )
                } else {
                    snackbarDelegate.showSnackbar(
                        message = e.message ?: "Ocurrio un error!"
                    )
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun goToSplitBillByProduct() {
        if (currentShift != null && currentShift.isFinished.not()) {
            AvoqadoApp.paymentRepository.setCachePaymentInfo(
                PaymentInfoResult(
                    paymentId = "",
                    tipAmount = 0.0,
                    subtotal = 0.0,
                    rootData = "",
                    date = LocalDateTime.now(),
                    waiterName = _tableDetail.value.waiterName,
                    tableNumber = tableNumber,
                    venueId = venueId,
                    splitType = SplitType.PERPRODUCT,
                    billId = _tableDetail.value.billId
                )
            )
            navigationDispatcher.navigateWithArgs(
                ManagementDests.SplitByProduct
            )
        } else {
            navigationDispatcher.navigateTo(ManagementDests.OpenShift)
        }

    }

    fun goToSplitBillByPerson() {
        if (currentShift != null && currentShift.isFinished.not()) {
            AvoqadoApp.paymentRepository.setCachePaymentInfo(
                PaymentInfoResult(
                    paymentId = "",
                    tipAmount = 0.0,
                    subtotal = 0.0,
                    rootData = "",
                    date = LocalDateTime.now(),
                    waiterName = _tableDetail.value.waiterName,
                    tableNumber = tableNumber,
                    venueId = venueId,
                    splitType = SplitType.EQUALPARTS,
                    billId = _tableDetail.value.billId
                )
            )
            navigationDispatcher.navigateWithArgs(
                ManagementDests.SplitByPerson
            )
        } else {
            navigationDispatcher.navigateTo(ManagementDests.OpenShift)
        }

    }

    fun payTotalPendingAmount() {
        if (currentShift != null && currentShift.isFinished.not()) {
            AvoqadoApp.paymentRepository.setCachePaymentInfo(
                PaymentInfoResult(
                    paymentId = "",
                    tipAmount = 0.0,
                    subtotal = _tableDetail.value.totalPending,
                    rootData = "",
                    date = LocalDateTime.now(),
                    waiterName = _tableDetail.value.waiterName,
                    tableNumber = tableNumber,
                    venueId = venueId,
                    splitType = SplitType.FULLPAYMENT,
                    billId = _tableDetail.value.billId
                )
            )
            navigationDispatcher.navigateWithArgs(
                PaymentDests.InputTip,
                NavigationArg.StringArg(
                    PaymentDests.InputTip.ARG_SUBTOTAL,
                    _tableDetail.value.totalPending.toString()
                ),
                NavigationArg.StringArg(
                    PaymentDests.InputTip.ARG_WAITER,
                    _tableDetail.value.waiterName
                ),
                NavigationArg.StringArg(
                    PaymentDests.InputTip.ARG_SPLIT_TYPE,
                    SplitType.FULLPAYMENT.value
                )
            )
        } else {
            navigationDispatcher.navigateTo(ManagementDests.OpenShift)
        }

    }

    fun payCustomPendingAmount(amount: Double) {
        if (currentShift != null && currentShift.isFinished.not()) {
            if (amount <= _tableDetail.value.totalPending) {
                AvoqadoApp.paymentRepository.setCachePaymentInfo(
                    PaymentInfoResult(
                        paymentId = "",
                        tipAmount = 0.0,
                        subtotal = amount,
                        rootData = "",
                        date = LocalDateTime.now(),
                        waiterName = _tableDetail.value.waiterName,
                        tableNumber = tableNumber,
                        venueId = venueId,
                        splitType = SplitType.CUSTOMAMOUNT,
                        billId = _tableDetail.value.billId
                    )
                )
                navigationDispatcher.navigateWithArgs(
                    PaymentDests.InputTip,
                    NavigationArg.StringArg(
                        PaymentDests.InputTip.ARG_SUBTOTAL,
                        amount.toString()
                    ),
                    NavigationArg.StringArg(
                        PaymentDests.InputTip.ARG_WAITER,
                        _tableDetail.value.waiterName
                    ),
                    NavigationArg.StringArg(
                        PaymentDests.InputTip.ARG_SPLIT_TYPE,
                        SplitType.CUSTOMAMOUNT.value
                    )
                )
            } else {
                snackbarDelegate.showSnackbar(
                    message = "El monto ingresado es mayor al total pendiente"
                )
            }
        } else {
            navigationDispatcher.navigateTo(ManagementDests.OpenShift)
        }
    }

}