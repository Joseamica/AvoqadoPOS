package com.avoqado.pos.features.management.presentation.splitProduct.model

import com.avoqado.pos.core.presentation.utils.toAmountMx
import com.avoqado.pos.features.management.presentation.tableDetail.model.Product

data class SplitByProductViewState(
    val isLoading: Boolean = false,
    val totalPending: String = "0.00",
    val products: List<Product> = emptyList(),
    val selectedProducts: List<String> = emptyList(),
    val waiterName: String = ""
) {
    val totalQuantitySelected: Int
        get() = selectedProducts.size

    val totalSelected: String
        get() = products.filter { product -> product.id in selectedProducts }
            .sumOf { it.totalPrice }.toString().toAmountMx()
}
