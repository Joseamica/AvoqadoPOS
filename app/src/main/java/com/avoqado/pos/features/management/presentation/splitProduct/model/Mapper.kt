package com.avoqado.pos.features.management.presentation.splitProduct.model

import com.avoqado.pos.core.presentation.utils.toAmountMx
import com.avoqado.pos.features.management.domain.models.Product
import com.avoqado.pos.features.management.domain.models.TableDetail

fun TableDetail.toUI(): SplitByProductViewState {
    return SplitByProductViewState(
        totalPending = totalPending.toString().toAmountMx(),
        products = products.map { it.toUI() },
        selectedProducts = emptyList(),
        waiterName = waiterName ?: "",
        paidProducts = this.paymentOverview?.paidProducts?: emptyList()
    )
}

fun Product.toUI(): com.avoqado.pos.core.presentation.model.Product {
    return com.avoqado.pos.core.presentation.model.Product(
        id = id,
        name = name,
        price = price,
        quantity = quantity,
        totalPrice = price * quantity
    )
}