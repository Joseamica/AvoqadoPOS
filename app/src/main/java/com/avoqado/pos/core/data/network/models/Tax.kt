package com.avoqado.pos.core.data.network.models

import com.google.gson.annotations.SerializedName

data class Tax(
    @SerializedName("id")
    val id: String?,
    @SerializedName("type")
    val type: String?,
)
