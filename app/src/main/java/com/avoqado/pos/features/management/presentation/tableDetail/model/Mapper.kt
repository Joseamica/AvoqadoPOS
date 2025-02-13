package com.avoqado.pos.features.management.presentation.tableDetail.model

import com.avoqado.pos.core.domain.models.SplitType
import com.avoqado.pos.features.management.domain.models.PaymentOverview

fun TableDetail.toDomain(): com.avoqado.pos.features.management.domain.models.TableDetail {
    return com.avoqado.pos.features.management.domain.models.TableDetail(
        id = this.tableId,
        name = this.name,
        products = this.products.map { it.toDomain() },
        totalAmount = this.totalAmount,
        totalPending = this.totalPending,
        waiterName = waiterName,
        paymentOverview = this.paymentsDone.takeIf { it.isNotEmpty() }?.let {
            val byPerson = it.filter { payment -> payment.splitType == SplitType.EQUALPARTS.value }
            PaymentOverview(
                paidProducts = it.filter { payment -> payment.splitType == SplitType.PERPRODUCT.value }
                    .map { payment ->
                        payment.products
                    }.flatten(),
                equalPartySize = byPerson.firstOrNull()?.equalPartsPartySize?.toInt() ?: 0,
                equalPartyPaidSize = byPerson.sumOf { payment ->
                    payment.equalPartsPayedFor?.toInt() ?: 0
                }
            )
        }
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