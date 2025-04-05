package com.avoqado.pos.core.data.network.models.bills


import com.google.gson.annotations.SerializedName

data class BillProduct(
    @SerializedName("id")
    val id: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("price")
    val price: String?,
    @SerializedName("quantity")
    val quantity: String?
)