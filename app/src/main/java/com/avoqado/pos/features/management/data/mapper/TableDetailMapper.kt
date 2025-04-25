package com.avoqado.pos.features.management.data.mapper

import com.avoqado.pos.features.management.data.model.PaymentOverviewEntity
import com.avoqado.pos.features.management.data.model.ProductCacheEntity
import com.avoqado.pos.features.management.data.model.TableCacheEntity
import com.avoqado.pos.features.management.domain.models.PaymentOverview
import com.avoqado.pos.features.management.domain.models.Product
import com.avoqado.pos.features.management.domain.models.TableDetail

fun TableCacheEntity.toDomain(): TableDetail =
    TableDetail(
        id = this.id,
        products = this.products.map { it.toDomain() },
        name = this.name,
        totalPending = totalPending,
        totalAmount = totalAmount,
        waiterName = waiterName,
        paymentOverview =
            paymentOverviewEntity?.let {
                PaymentOverview(
                    equalPartySize = it.equalPartySize,
                    equalPartyPaidSize = it.equalPartyPaidSize,
                    paidProducts = it.paidProducts,
                )
            },
    )

fun TableDetail.toCache(): TableCacheEntity =
    TableCacheEntity(
        id = this.id,
        name = this.name,
        products = this.products.map { it.toCache() },
        totalPending = this.totalPending,
        totalAmount = this.totalAmount,
        waiterName = waiterName,
        paymentOverviewEntity =
            paymentOverview?.let {
                PaymentOverviewEntity(
                    paidProducts = it.paidProducts,
                    equalPartySize = it.equalPartySize,
                    equalPartyPaidSize = it.equalPartyPaidSize,
                )
            },
    )

fun ProductCacheEntity.toDomain(): Product =
    Product(
        id = this.id,
        name = this.name,
        price = this.price,
        quantity = this.quantity,
    )

fun Product.toCache(): ProductCacheEntity =
    ProductCacheEntity(
        id = this.id,
        name = this.name,
        price = this.price,
        quantity = this.quantity,
        totalPrice = this.price * this.quantity,
    )
