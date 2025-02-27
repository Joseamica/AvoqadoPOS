package com.avoqado.pos.core.data.network.models


import com.google.gson.annotations.SerializedName

data class Address(
    @SerializedName("apartment")
    val apartment: String?,
    @SerializedName("city")
    val city: String?,
    @SerializedName("floor")
    val floor: String?,
    @SerializedName("number")
    val number: String?,
    @SerializedName("state")
    val state: String?,
    @SerializedName("street")
    val street: String?,
    @SerializedName("zip")
    val zip: String?
)