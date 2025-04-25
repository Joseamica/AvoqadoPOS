package com.avoqado.pos.features.payment.data.network.models

import com.google.gson.annotations.SerializedName

data class Product(
    @SerializedName("available")
    val available: Any?,
    @SerializedName("billId")
    val billId: String,
    @SerializedName("byAvoqado")
    val byAvoqado: Boolean,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("discount")
    val discount: Any?,
    @SerializedName("id")
    val id: String,
    @SerializedName("idproducto")
    val idproducto: String,
    @SerializedName("key")
    val key: Any?,
    @SerializedName("modifier")
    val modifier: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("paid")
    val paid: Boolean,
    @SerializedName("paymentId")
    val paymentId: Any?,
    @SerializedName("posOrder")
    val posOrder: Any?,
    @SerializedName("price")
    val price: String,
    @SerializedName("productType")
    val productType: Any?,
    @SerializedName("punitario")
    val punitario: Any?,
    @SerializedName("quantity")
    val quantity: Int,
    @SerializedName("rewardProductId")
    val rewardProductId: Any?,
    @SerializedName("sequence")
    val sequence: Any?,
    @SerializedName("tax")
    val tax: Any?,
    @SerializedName("type")
    val type: Any?,
    @SerializedName("updatedAt")
    val updatedAt: String,
    @SerializedName("venueId")
    val venueId: String,
    @SerializedName("waiterId")
    val waiterId: String?,
    @SerializedName("waiterName")
    val waiterName: String?,
)
