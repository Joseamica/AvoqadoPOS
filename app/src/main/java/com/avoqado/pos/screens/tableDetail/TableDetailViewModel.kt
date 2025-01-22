package com.avoqado.pos.screens.tableDetail

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avoqado.pos.core.delegates.SnackbarDelegate
import com.avoqado.pos.core.navigation.NavigationArg
import com.avoqado.pos.core.navigation.NavigationDispatcher
import com.avoqado.pos.core.utils.toAmountMXDouble
import com.avoqado.pos.core.utils.toAmountMx
import com.avoqado.pos.data.network.AvoqadoAPI
import com.avoqado.pos.destinations.MainDests
import com.avoqado.pos.screens.tableDetail.model.Product
import com.avoqado.pos.screens.tableDetail.model.TableDetail
import com.menta.android.core.utils.StringUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class TableDetailViewModel(
    private val tableNumber: String="",
    private val venueId: String="",
    private val navigationDispatcher: NavigationDispatcher,
    private val snackbarDelegate: SnackbarDelegate
) : ViewModel() {
    private val _tableDetail = MutableStateFlow<TableDetail>(TableDetail())
    val tableDetail: StateFlow<TableDetail> = _tableDetail.asStateFlow()

    private val _showPaymentPicker = MutableStateFlow(false)
    val showPaymentPicker: StateFlow<Boolean> = _showPaymentPicker.asStateFlow()

    fun navigateBack() {
        navigationDispatcher.navigateBack()
    }

    fun togglePaymentPicker() {
        _showPaymentPicker.value = _showPaymentPicker.value.not()
    }

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        fetchTableDetail()
    }

    fun fetchTableDetail() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            try {
                val result = AvoqadoAPI.apiService.getVenueTableDetail(
                    venueId = venueId,
                    tableNumber = tableNumber
                )


                _tableDetail.value = TableDetail(
                    tableId = tableNumber,
                    name = "Mesa $tableNumber",
                )

                val billDetail = AvoqadoAPI.apiService.getTableBill(
                    venueId = venueId,
                    billId = result.table?.bill?.id ?: ""
                )
                _tableDetail.value = _tableDetail.value.copy(
                    totalAmount = billDetail.total.toString().toAmountMXDouble(),
                    totalPending = billDetail.amountLeft.toString().toAmountMXDouble(),
                    products = billDetail.products.groupBy { it.name }.map { pair ->
                        val item = pair.value.first()
                        Product(
                            id = item.name,
                            name = item.name,
                            price = pair.value.maxOf { it.price.toAmountMXDouble() },
                            quantity = pair.value.sumOf { it.quantity },
                            totalPrice = pair.value.sumOf { it.price.toAmountMXDouble() }
                        )
                    },
                )
            } catch (e: Exception) {
                Log.i("TableDetailViewModel", "Error fetching table detail", e)
                if (e is HttpException) {
                    snackbarDelegate.showSnackbar(
                        message = e.message() ?: "Ocurrio un error!"
                    )
                } else  {
                    snackbarDelegate.showSnackbar(
                        message = e.message ?: "Ocurrio un error!"
                    )
                }
            }
            finally {
                _isLoading.value = false
            }
        }
    }

    fun goToPayment(type: String) {
        when (type) {
            "total" -> {
                _showPaymentPicker.value = false
                navigationDispatcher.navigateWithArgs(
                    MainDests.InputTip,
                    NavigationArg.StringArg(
                        MainDests.InputTip.ARG_SUBTOTAL,
                        _tableDetail.value.totalPending.toString()
                    )
                )
            }

            //TODO: agregar otras formas de pago
            else -> {
                _showPaymentPicker.value = false
                snackbarDelegate.showSnackbar(
                    message = "Forma de pago en desarrollo"
                )
            }
        }
    }
}