package com.avoqado.pos.core.data.network.models.bills

import com.google.gson.annotations.SerializedName

data class BillProductDetail(
    @SerializedName("available")
    val available: Any?,
    @SerializedName("billId")
    val billId: Any?,
    @SerializedName("billV2Id")
    val billV2Id: String?,
    @SerializedName("byAvoqado")
    val byAvoqado: Boolean?,
    @SerializedName("createdAt")
    val createdAt: String?,
    @SerializedName("discount")
    val discount: String?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("idproducto")
    val idproducto: String?,
    @SerializedName("key")
    val key: String?,
    @SerializedName("modifier")
    val modifier: Int?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("paid")
    val paid: Boolean?,
    @SerializedName("paymentId")
    val paymentId: Any?,
    @SerializedName("posOrder")
    val posOrder: Int?,
    @SerializedName("price")
    val price: String?,
    @SerializedName("productType")
    val productType: Any?,
    @SerializedName("punitario")
    val punitario: Any?,
    @SerializedName("quantity")
    val quantity: String?,
    @SerializedName("rewardProductId")
    val rewardProductId: Any?,
    @SerializedName("sequence")
    val sequence: Any?,
    @SerializedName("status")
    val status: Any?,
    @SerializedName("tax")
    val tax: Any?,
    @SerializedName("type")
    val type: String?,
    @SerializedName("uniqueCodeFromPos")
    val uniqueCodeFromPos: String?,
    @SerializedName("updatedAt")
    val updatedAt: String?,
    @SerializedName("venueId")
    val venueId: String?,
    @SerializedName("waiterId")
    val waiterId: String?,
    @SerializedName("waiterName")
    val waiterName: String?,
)
