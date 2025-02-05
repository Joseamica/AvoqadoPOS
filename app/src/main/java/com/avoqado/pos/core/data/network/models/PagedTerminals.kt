package com.avoqado.pos.core.data.network.models


import com.google.gson.annotations.SerializedName

data class PagedTerminals(
    @SerializedName("_embedded")
    val embedded: Embedded,
    @SerializedName("page")
    val page: Page
)