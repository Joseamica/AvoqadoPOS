package com.avoqado.pos.features.management.presentation.tableDetail.model

fun TableDetail.toDomain() : com.avoqado.pos.features.management.domain.models.TableDetail{
    return com.avoqado.pos.features.management.domain.models.TableDetail(
        id = this.tableId,
        name = this.name,
        products = this.products.map { it.toDomain() },
        totalAmount = this.totalAmount,
        totalPending = this.totalPending,
        waiterName = waiterName
    )
}

fun Product.toDomain(): com.avoqado.pos.features.management.domain.models.Product {
    return com.avoqado.pos.features.management.domain.models.Product(
        id = this.id,
        name = this.name,
        price = this.price,
        quantity = this.quantity
    )
}