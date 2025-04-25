package com.avoqado.pos.core.data.network.models

import com.google.gson.annotations.SerializedName

data class RepresentativeId(
    @SerializedName("number")
    val number: String?,
    @SerializedName("type")
    val type: String?,
)
