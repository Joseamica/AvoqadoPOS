package com.avoqado.pos.core.data.network.models.transactions.payments

import com.google.gson.annotations.SerializedName

data class Tpv(
    @SerializedName("id")
    val id: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("serial")
    val serial: String?,
)
