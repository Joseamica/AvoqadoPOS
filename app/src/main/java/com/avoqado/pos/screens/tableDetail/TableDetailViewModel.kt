package com.avoqado.pos.screens.tableDetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.avoqado.pos.core.navigation.NavigationArg
import com.avoqado.pos.core.navigation.NavigationDispatcher
import com.avoqado.pos.destinations.MainDests
import com.avoqado.pos.screens.tableDetail.model.TableDetail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TableDetailViewModel (
    private val savedStateHandle: SavedStateHandle,
    private val navigationDispatcher: NavigationDispatcher
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

    fun getTableDetail(){

    }

    fun goToPayment(type: String){
        when(type) {
            "total" -> {
                navigationDispatcher.navigateWithArgs(
                    MainDests.InputTip,
                    NavigationArg.StringArg(
                        MainDests.InputTip.ARG_SUBTOTAL,
                        _tableDetail.value.formattedTotalPrice
                    )
                )
            }
        }
    }
}