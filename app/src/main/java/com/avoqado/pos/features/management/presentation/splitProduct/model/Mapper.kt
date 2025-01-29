package com.avoqado.pos.features.management.presentation.splitProduct.model

import com.avoqado.pos.core.utils.toAmountMx
import com.avoqado.pos.features.management.domain.models.Product
import com.avoqado.pos.features.management.domain.models.TableDetail

fun TableDetail.toUI(): SplitByProductViewState {
    return SplitByProductViewState(
        totalPending = totalPending.toString().toAmountMx(),
        products = products.map { it.toUI() },
        selectedProducts = emptyList(),
        waiterName = waiterName ?: ""
    )
}

fun Product.toUI(): com.avoqado.pos.features.management.presentation.tableDetail.model.Product {
    return com.avoqado.pos.features.management.presentation.tableDetail.model.Product(
        id = id,
        name = name,
        price = price,
        quantity = quantity,
        totalPrice = price * quantity
    )
}