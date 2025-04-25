package com.avoqado.pos.core.data.network.models.bills

import com.google.gson.annotations.SerializedName

data class Waiter(
    @SerializedName("id")
    val id: String?,
    @SerializedName("idmesero")
    val idmesero: String?,
    @SerializedName("nombre")
    val nombre: String?,
)
