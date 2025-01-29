package com.avoqado.pos.features.management.data.mapper

import com.avoqado.pos.features.management.data.model.ProductCacheEntity
import com.avoqado.pos.features.management.data.model.TableCacheEntity
import com.avoqado.pos.features.management.domain.models.Product
import com.avoqado.pos.features.management.domain.models.TableDetail

fun TableCacheEntity.toDomain() : TableDetail {
    return TableDetail(
        id = this.id,
        products = this.products.map { it.toDomain() },
        name = this.name,
        totalPending = totalPending,
        totalAmount = totalAmount,
        waiterName = waiterName
    )
}

fun TableDetail.toCache(): TableCacheEntity {
    return TableCacheEntity(
        id = this.id,
        name = this.name,
        products = this.products.map { it.toCache() },
        totalPending = this.totalPending,
        totalAmount = this.totalAmount,
        waiterName = waiterName
    )
}

fun ProductCacheEntity.toDomain(): Product {
    return Product(
        id = this.id,
        name = this.name,
        price = this.price,
        quantity = this.quantity
    )
}

fun Product.toCache(): ProductCacheEntity {
    return ProductCacheEntity(
        id = this.id,
        name = this.name,
        price = this.price,
        quantity = this.quantity,
        totalPrice = this.price * this.quantity
    )
}