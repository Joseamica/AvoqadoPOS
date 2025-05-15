package com.avoqado.pos.core.data.network.models

import com.google.gson.annotations.SerializedName

data class Feature(
    @SerializedName("ordering")
    val ordering: Boolean = false
)
