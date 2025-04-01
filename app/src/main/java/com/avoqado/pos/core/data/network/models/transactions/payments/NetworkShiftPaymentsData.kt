package com.avoqado.pos.core.data.network.models.transactions.payments


import com.google.gson.annotations.SerializedName

data class NetworkShiftPaymentsData(
    @SerializedName("data")
    val `data`: List<Data?>?,
    @SerializedName("meta")
    val meta: Meta?,
    @SerializedName("success")
    val success: Boolean?
)