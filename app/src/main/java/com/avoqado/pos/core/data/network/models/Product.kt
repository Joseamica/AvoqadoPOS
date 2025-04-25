package com.avoqado.pos.core.data.network.models

import com.google.gson.annotations.SerializedName

data class Product(
    @SerializedName("discount")
    val discount: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("key")
    val key: String?,
    @SerializedName("modifier")
    val modifier: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("paid")
    val paid: Boolean,
    @SerializedName("price")
    val price: String,
    @SerializedName("quantity")
    val quantity: Int,
)
