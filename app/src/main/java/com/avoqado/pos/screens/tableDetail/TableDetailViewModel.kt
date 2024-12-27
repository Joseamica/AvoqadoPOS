package com.avoqado.pos.screens.tableDetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avoqado.pos.core.delegates.SnackbarDelegate
import com.avoqado.pos.core.navigation.NavigationArg
import com.avoqado.pos.core.navigation.NavigationDispatcher
import com.avoqado.pos.data.network.AvoqadoAPI
import com.avoqado.pos.destinations.MainDests
import com.avoqado.pos.screens.tableDetail.model.TableDetail
import com.menta.android.core.utils.StringUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TableDetailViewModel (
    private val savedStateHandle: SavedStateHandle,
    private val navigationDispatcher: NavigationDispatcher,
    private val snackbarDelegate: SnackbarDelegate
) : ViewModel() {

    private val tableNumber = savedStateHandle.get<String>(MainDests.TableDetail.ARG_TABLE_ID) ?: ""
    private val venueId = savedStateHandle.get<String>(MainDests.TableDetail.ARG_VENUE_ID) ?: ""

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

    fun getTableDetail(){
        viewModelScope.launch(Dispatchers.IO) {
            val result = AvoqadoAPI.apiService.getVenueTableDetail(venueId = venueId, tableNumber = tableNumber)

            _tableDetail.value = TableDetail(
                tableId = tableNumber,
                name = "Mesa $tableNumber",
                totalAmount = StringUtils.toDoubleAmount(result.table?.bill?.total ?: "0.00")
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