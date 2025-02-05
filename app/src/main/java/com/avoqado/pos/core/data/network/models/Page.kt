package com.avoqado.pos.core.data.network.models


import com.google.gson.annotations.SerializedName

data class Page(
    @SerializedName("number")
    val number: Int,
    @SerializedName("size")
    val size: Int,
    @SerializedName("total_elements")
    val totalElements: Int,
    @SerializedName("total_pages")
    val totalPages: Int
)