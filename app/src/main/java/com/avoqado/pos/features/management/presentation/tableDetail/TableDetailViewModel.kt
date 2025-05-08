package com.avoqado.pos.features.management.presentation.tableDetail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avoqado.pos.AvoqadoApp
import com.avoqado.pos.core.domain.models.AvoqadoError
import com.avoqado.pos.core.domain.models.SplitType
import com.avoqado.pos.core.presentation.delegates.SnackbarDelegate
import com.avoqado.pos.core.presentation.delegates.SnackbarState
import com.avoqado.pos.core.presentation.model.Product
import com.avoqado.pos.core.presentation.navigation.NavigationArg
import com.avoqado.pos.core.presentation.navigation.NavigationDispatcher
import com.avoqado.pos.features.management.domain.ManagementRepository
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
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

class TableDetailViewModel(
    private val tableNumber: String = "",
    private val venueId: String = "",
    private val navigationDispatcher: NavigationDispatcher,
    private val snackbarDelegate: SnackbarDelegate,
    private val managementRepository: ManagementRepository,
) : ViewModel() {
    val venue = AvoqadoApp.sessionManager.getVenueInfo()
    var currentShift = AvoqadoApp.sessionManager.getShift()
    
    // Get a reference to the socket service
    private val socketService by lazy { AvoqadoApp.socketService }

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _tableDetail = MutableStateFlow<TableDetailView>(TableDetailView())
    val tableDetail: StateFlow<TableDetailView> = _tableDetail.asStateFlow()

    private val _showPaymentPicker = MutableStateFlow(false)
    val showPaymentPicker: StateFlow<Boolean> = _showPaymentPicker.asStateFlow()
    
    // Track if we're collecting socket events
    private var isCollectingSocketEvents = false
    
    // Track if the bill has been deleted or is no longer available
    private var isBillDeleted = false

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
            if (venueId.isEmpty() || tableNumber.isEmpty()) {
                Log.e(TAG, "Cannot listen for updates: venueId or tableNumber is empty")
                return@launch
            }
            
            try {
                Log.d(TAG, "Starting to listen for updates - Venue: $venueId, Table: $tableNumber")
                
                // Join the table room using the SocketService
                socketService?.joinTableRoom(venueId, tableNumber)
                Log.d(TAG, "Joined table room for venue: $venueId, table: $tableNumber")
                
                // Only start collecting if we're not already
                if (!isCollectingSocketEvents) {
                    Log.d(TAG, "Starting to collect socket events")
                    isCollectingSocketEvents = true
                    
                    // Collect the table-specific message flow
                    socketService?.messageFlow?.collectLatest { update ->
                        Log.d(TAG, "Socket event received - Raw update: $update")
                        Log.d(TAG, "Current table number: $tableNumber, Current venue ID: $venueId")
                        Log.d(TAG, "Update status: ${update.status}")

                        // Check for bill status changes using status field
                        when (update.status?.uppercase()) {
                            "DELETED" -> {
                                Log.d(TAG, "Bill DELETED - navigating back")
                                // Mark the bill as deleted to prevent further API calls
                                isBillDeleted = true
                                // Stop socket listener since the bill is gone
                                stopListeningUpdates()
                                snackbarDelegate.showSnackbar(
                                    state = SnackbarState.Default,
                                    message = "La cuenta ha sido eliminada",
                                )
                                viewModelScope.launch(Dispatchers.Main) {
                                    navigateBack()
                                }
                            }
                            "CANCELED" -> {
                                Log.d(TAG, "Bill CANCELED - navigating back")
                                // Mark the bill as deleted to prevent further API calls
                                isBillDeleted = true
                                // Stop socket listener since the bill is gone
                                stopListeningUpdates()
                                snackbarDelegate.showSnackbar(
                                    state = SnackbarState.Default,
                                    message = "La cuenta ha sido cancelada",
                                )
                                viewModelScope.launch(Dispatchers.Main) {
                                    navigateBack()
                                }
                            }
                            "PAID" -> {
                                Log.d(TAG, "Bill PAID - showing info and navigating back")
                                // For paid bills, backend returns 404 since it only finds OPEN bills
                                // So we should mark the bill as deleted to prevent further API calls
                                isBillDeleted = true
                                
                                // Show success message
                                snackbarDelegate.showSnackbar(
                                    state = SnackbarState.Default,
                                    message = "La cuenta ha sido pagada",
                                )
                                
                                viewModelScope.launch(Dispatchers.Main) {
                                    // First show any necessary success UI
                                    // Wait a moment before navigating back to allow user to see the message
                                    kotlinx.coroutines.delay(1500)
                                    // Then navigate back since the bill is no longer available
                                    navigateBack()
                                }
                            }
                            "PRODUCT_ADDED" -> {
                                Log.d(TAG, "Product added event received - Starting refresh")
                                viewModelScope.launch(Dispatchers.Main) {
                                    Log.d(TAG, "Fetching table detail after PRODUCT_ADDED")
                                    fetchTableDetail()
                                    Log.d(TAG, "Table detail refresh completed after PRODUCT_ADDED")
                                }
                                snackbarDelegate.showSnackbar(
                                    state = SnackbarState.Default,
                                    message = "La cuenta ha sido actualizada",
                                )
                            }
                            "UPDATED", "PRODUCT_UPDATED", "PRODUCT_REMOVED" -> {
                                Log.d(TAG, "Products updated - refreshing table detail")
                                viewModelScope.launch(Dispatchers.Main) {
                                    Log.d(TAG, "Fetching table detail after product update")
                                    fetchTableDetail()
                                    Log.d(TAG, "Table detail refresh completed after product update")
                                }
                                snackbarDelegate.showSnackbar(
                                    state = SnackbarState.Default,
                                    message = "La cuenta ha sido actualizada",
                                )
                            }
                            else -> {
                                Log.d(TAG, "Unhandled status: ${update.status} - refreshing anyway")
                                viewModelScope.launch(Dispatchers.Main) {
                                    Log.d(TAG, "Fetching table detail for unhandled status")
                                    fetchTableDetail()
                                    Log.d(TAG, "Table detail refresh completed for unhandled status")
                                }
                            }
                        }
                    }
                } else {
                    Log.d(TAG, "Already collecting socket events - skipping collection setup")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error setting up socket updates", e)
                isCollectingSocketEvents = false
            }
        }
    }

    fun refreshShift() {
        currentShift = AvoqadoApp.sessionManager.getShift()
    }

    fun stopListeningUpdates() {
        // No need to explicitly disconnect from the socket - the SocketService manages connections
        isCollectingSocketEvents = false
    }

    fun fetchTableDetail() {
        // Don't fetch if the bill has been deleted
        if (isBillDeleted) {
            Log.d(TAG, "Skipping fetchTableDetail because bill is deleted")
            return
        }
        
        viewModelScope.launch {
            _isLoading.value = true
            try {
                Log.d(TAG, "Starting table detail fetch for table: $tableNumber, venue: $venueId")
                val billDetail = withContext(Dispatchers.IO) {
                    managementRepository.getDetailedBill(
                        venueId = venueId,
                        billId = tableNumber,
                    )
                }
                
                Log.d(TAG, "Successfully fetched bill detail: $billDetail")
                
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
                                totalPrice = item.price,
                            )
                        },
                        paymentsDone = billDetail.paymentsDone.map { payment ->
                            Payment(
                                amount = payment.amount,
                                products = payment.products,
                                splitType = payment.splitType,
                                equalPartsPayedFor = payment.equalPartsPayedFor,
                                equalPartsPartySize = payment.equalPartsPartySize,
                            )
                        },
                        currentSplitType = billDetail.paymentsDone.lastOrNull()?.splitType?.let {
                            SplitType.valueOf(it)
                        },
                    )
                }

                Log.d(TAG, "Updated table detail state with new data")
                managementRepository.setTableCache(_tableDetail.value.toDomain())
                
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching table detail", e)
                
                // Check if the bill was not found (likely deleted)
                if (e is AvoqadoError.BasicError && (e.code == 404 || e.code == 400)) {
                    Log.d(TAG, "Bill not found (HTTP ${e.code}) - likely deleted")
                    // Mark the bill as deleted to prevent further API calls
                    isBillDeleted = true
                    // Stop socket listener since the bill is gone
                    stopListeningUpdates()
                    snackbarDelegate.showSnackbar(
                        state = SnackbarState.Default,
                        message = "La cuenta ya no existe",
                    )
                    navigateBack()
                } else {
                    // Other errors
                    snackbarDelegate.showSnackbar(
                        state = SnackbarState.Default,
                        message = e.message ?: "Ocurrio un error!",
                    )
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun goToSplitBillByProduct() {
        if (currentShift != null && currentShift?.isFinished?.not() == true) {
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
                    billId = _tableDetail.value.billId,
                ),
            )
            navigationDispatcher.navigateWithArgs(
                ManagementDests.SplitByProduct,
            )
        } else {
            navigationDispatcher.navigateTo(ManagementDests.OpenShift)
        }
    }

    fun goToSplitBillByPerson() {
        if (currentShift != null && currentShift?.isFinished?.not() == true) {
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
                    billId = _tableDetail.value.billId,
                ),
            )
            navigationDispatcher.navigateWithArgs(
                ManagementDests.SplitByPerson,
            )
        } else {
            navigationDispatcher.navigateTo(ManagementDests.OpenShift)
        }
    }

    fun payTotalPendingAmount() {
        if (currentShift != null && currentShift?.isFinished?.not() == true) {
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
                    billId = _tableDetail.value.billId,
                ),
            )
            navigationDispatcher.navigateWithArgs(
                PaymentDests.LeaveReview,
                NavigationArg.StringArg(
                    PaymentDests.LeaveReview.ARG_SUBTOTAL,
                    _tableDetail.value.totalPending.toString(),
                ),
                NavigationArg.StringArg(
                    PaymentDests.LeaveReview.ARG_WAITER,
                    _tableDetail.value.waiterName,
                ),
                NavigationArg.StringArg(
                    PaymentDests.LeaveReview.ARG_SPLIT_TYPE,
                    SplitType.FULLPAYMENT.value,
                ),
                NavigationArg.StringArg(
                    PaymentDests.LeaveReview.ARG_VENUE_NAME,
                    venue?.name ?: "",
                ),
            )
        } else {
            navigationDispatcher.navigateTo(ManagementDests.OpenShift)
        }
    }

    fun payCustomPendingAmount(amount: Double) {
        if (currentShift != null && currentShift?.isFinished?.not() == true) {
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
                        billId = _tableDetail.value.billId,
                    ),
                )
                navigationDispatcher.navigateWithArgs(
                    PaymentDests.LeaveReview,
                    NavigationArg.StringArg(
                        PaymentDests.LeaveReview.ARG_SUBTOTAL,
                        amount.toString(),
                    ),
                    NavigationArg.StringArg(
                        PaymentDests.LeaveReview.ARG_WAITER,
                        _tableDetail.value.waiterName,
                    ),
                    NavigationArg.StringArg(
                        PaymentDests.LeaveReview.ARG_SPLIT_TYPE,
                        SplitType.CUSTOMAMOUNT.value,
                    ),
                    NavigationArg.StringArg(
                        PaymentDests.LeaveReview.ARG_VENUE_NAME,
                        venue?.name ?: "",
                    ),
                )
            } else {
                snackbarDelegate.showSnackbar(
                    state = SnackbarState.Default,
                    message = "El monto ingresado es mayor al total pendiente",
                )
            }
        } else {
            navigationDispatcher.navigateTo(ManagementDests.OpenShift)
        }
    }

    companion object {
        private const val TAG = "TableSocket"
    }
}
