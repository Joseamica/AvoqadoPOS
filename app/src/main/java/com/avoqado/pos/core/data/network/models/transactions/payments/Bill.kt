package com.avoqado.pos.core.data.network.models.transactions.payments

import com.google.gson.annotations.SerializedName

data class Bill(
    @SerializedName("folio")
    val folio: String?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("total")
    val total: String?,
)
