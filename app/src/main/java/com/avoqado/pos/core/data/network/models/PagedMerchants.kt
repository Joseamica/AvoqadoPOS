package com.avoqado.pos.core.data.network.models

import com.google.gson.annotations.SerializedName

data class PagedMerchants(
    @SerializedName("_embedded")
    val embedded: Embedded,
    @SerializedName("page")
    val page: Page,
)
