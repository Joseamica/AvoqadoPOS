package com.avoqado.pos.features.management.presentation.splitProduct

import androidx.lifecycle.ViewModel
import com.avoqado.pos.core.navigation.NavigationDispatcher
import com.avoqado.pos.features.management.domain.ManagementRepository
import com.avoqado.pos.features.management.presentation.splitProduct.model.SplitByProductViewState
import com.avoqado.pos.features.management.presentation.splitProduct.model.toUI
import com.avoqado.pos.features.management.presentation.tableDetail.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SplitByProductViewModel constructor(
    private val navigationDispatcher: NavigationDispatcher,
    private val managementRepository: ManagementRepository
): ViewModel() {
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

    fun onProductItemTapped(product: Product){
        if (_tableDetail.value.selectedProducts.contains(product.id)){
            _tableDetail.update {
                it.copy(selectedProducts = it.selectedProducts - product.id)
            }
        } else {
            _tableDetail.update {
                it.copy(selectedProducts = it.selectedProducts + product.id)
            }
        }
    }

    fun navigateToPayment(){

    }
}