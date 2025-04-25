package com.avoqado.pos.features.management.presentation.splitProduct

import androidx.lifecycle.ViewModel
import com.avoqado.pos.core.domain.models.SplitType
import com.avoqado.pos.core.presentation.model.Product
import com.avoqado.pos.core.presentation.navigation.NavigationArg
import com.avoqado.pos.core.presentation.navigation.NavigationDispatcher
import com.avoqado.pos.features.management.domain.ManagementRepository
import com.avoqado.pos.features.management.presentation.splitProduct.model.SplitByProductViewState
import com.avoqado.pos.features.management.presentation.splitProduct.model.toUI
import com.avoqado.pos.features.payment.domain.repository.PaymentRepository
import com.avoqado.pos.features.payment.presentation.navigation.PaymentDests
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SplitByProductViewModel constructor(
    private val navigationDispatcher: NavigationDispatcher,
    private val managementRepository: ManagementRepository,
    private val paymentRepository: PaymentRepository,
) : ViewModel() {
    private val _tableDetail = MutableStateFlow<SplitByProductViewState>(SplitByProductViewState())
    val tableDetail: StateFlow<SplitByProductViewState> = _tableDetail.asStateFlow()

    init {
        managementRepository.getCachedTable()?.let { table ->
            _tableDetail.update {
                table.toUI()
            }
        }
    }

    fun navigateBack() {
        navigationDispatcher.navigateBack()
    }

    fun onProductItemTapped(product: Product) {
        if (_tableDetail.value.selectedProducts.contains(product.id)) {
            _tableDetail.update {
                it.copy(selectedProducts = it.selectedProducts - product.id)
            }
        } else {
            _tableDetail.update {
                it.copy(selectedProducts = it.selectedProducts + product.id)
            }
        }
    }

    fun navigateToPayment() {
        paymentRepository.getCachePaymentInfo()?.let { info ->
            paymentRepository.setCachePaymentInfo(
                info.copy(
                    products = _tableDetail.value.selectedProducts,
                ),
            )
        }

        navigationDispatcher.navigateWithArgs(
            PaymentDests.InputTip,
            NavigationArg.StringArg(
                PaymentDests.InputTip.ARG_SUBTOTAL,
                _tableDetail.value.totalSelected,
            ),
            NavigationArg.StringArg(
                PaymentDests.InputTip.ARG_WAITER,
                _tableDetail.value.waiterName,
            ),
            NavigationArg.StringArg(
                PaymentDests.InputTip.ARG_SPLIT_TYPE,
                SplitType.PERPRODUCT.value,
            ),
        )
    }
}
