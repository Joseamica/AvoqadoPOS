package com.avoqado.pos.core.data.network.models.bills

import com.avoqado.pos.features.management.data.network.models.PaymentItemNetwork
import com.google.gson.annotations.SerializedName

data class NetworkBillDetailV2(
    @SerializedName("billName")
    val billName: String?,
    @SerializedName("createdAt")
    val createdAt: String?,
    @SerializedName("discount")
    val discount: Int?,
    @SerializedName("equalPartsId")
    val equalPartsId: Any?,
    @SerializedName("folio")
    val folio: String?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("isRenamedFromPos")
    val isRenamedFromPos: Boolean?,
    @SerializedName("isSplit")
    val isSplit: Boolean?,
    @SerializedName("isSplittedFromPos")
    val isSplittedFromPos: Boolean?,
    @SerializedName("key")
    val key: String?,
    @SerializedName("orderFromPos")
    val orderFromPos: Int?,
    @SerializedName("originalFolio")
    val originalFolio: Any?,
    @SerializedName("payments")
    val payments: List<PaymentItemNetwork?>?,
    @SerializedName("printed")
    val printed: Boolean?,
    @SerializedName("products")
    val products: List<BillProductDetail>?,
    @SerializedName("qrCode")
    val qrCode: Any?,
    @SerializedName("shiftId")
    val shiftId: Any?,
    @SerializedName("splitFolios")
    val splitFolios: List<String>?,
    @SerializedName("splitType")
    val splitType: Any?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("tableName")
    val tableName: String?,
    @SerializedName("total")
    val total: String?,
    @SerializedName("uniqueCodeFromPos")
    val uniqueCodeFromPos: String?,
    @SerializedName("updatedAt")
    val updatedAt: String?,
    @SerializedName("usertableId")
    val usertableId: Any?,
    @SerializedName("venueId")
    val venueId: String?,
    @SerializedName("waiterId")
    val waiterId: String?,
    @SerializedName("waiterName")
    val waiterName: String?,
)
