package com.avoqado.pos.screens.tableDetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avoqado.pos.core.delegates.SnackbarDelegate
import com.avoqado.pos.core.navigation.NavigationArg
import com.avoqado.pos.core.navigation.NavigationDispatcher
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

class TableDetailViewModel (
    private val tableNumber: String,
    private val venueId: String,
    private val navigationDispatcher: NavigationDispatcher,
    private val snackbarDelegate: SnackbarDelegate
) : ViewModel() {

    private val _tableDetail = MutableStateFlow<TableDetail>(TableDetail())
    val tableDetail: StateFlow<TableDetail> = _tableDetail.asStateFlow()

    private val _showPaymentPicker = MutableStateFlow(false)
    val showPaymentPicker: StateFlow<Boolean> = _showPaymentPicker.asStateFlow()

    fun navigateBack(){
        navigationDispatcher.navigateBack()
    }

    fun togglePaymentPicker(){
        _showPaymentPicker.value = _showPaymentPicker.value.not()
    }

    init {
        fetchTableDetail()
    }

    fun fetchTableDetail(){
        viewModelScope.launch(Dispatchers.IO) {
            val result = AvoqadoAPI.apiService.getVenueTableDetail(venueId = venueId, tableNumber = tableNumber)

            _tableDetail.value = TableDetail(
                tableId = tableNumber,
                name = "Mesa $tableNumber",
                totalAmount = StringUtils.toDoubleAmount(StringUtils.notFormatAmount(result.table?.bill?.total ?: "0.00"))
            )

            val billDetail = AvoqadoAPI.apiService.getTableBill(venueId = venueId, billId = result.table?.bill?.id ?: "")
            _tableDetail.value = _tableDetail.value.copy(
                products = billDetail.products.map {
                    Product(
                        id = it.id ?: "",
                        name = it.name ?: "",
                        price = StringUtils.toDoubleAmount(StringUtils.notFormatAmount(it.price)),
                        quantity = it.quantity ?: 0
                    )
                },
            )
        }
    }

    fun goToPayment(type: String){
        when(type) {
            "total" -> {
                _showPaymentPicker.value = false
                navigationDispatcher.navigateWithArgs(
                    MainDests.InputTip,
                    NavigationArg.StringArg(
                        MainDests.InputTip.ARG_SUBTOTAL,
                        _tableDetail.value.formattedTotalPrice
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