package com.avoqado.pos.data.network.models


import com.google.gson.annotations.SerializedName

data class Embedded(
    @SerializedName("terminals")
    val terminals: List<Terminal>
)