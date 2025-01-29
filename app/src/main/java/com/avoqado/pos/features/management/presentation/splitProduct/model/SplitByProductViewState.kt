package com.avoqado.pos.features.management.presentation.splitProduct.model

import com.avoqado.pos.core.utils.toAmountMx
import com.avoqado.pos.features.management.presentation.tableDetail.model.Product

data class SplitByProductViewState(
    val isLoading: Boolean = false,
    val totalPending: String = "0.00",
    val products: List<Product> = emptyList(),
    val selectedProducts: List<String> = emptyList()
) {
    val totalQuantitySelected: Int
        get() = selectedProducts.size

    val totalSelected: String
        get() = products.filter { product -> product.id in selectedProducts }
            .sumOf { it.totalPrice }.toString().toAmountMx()
}
