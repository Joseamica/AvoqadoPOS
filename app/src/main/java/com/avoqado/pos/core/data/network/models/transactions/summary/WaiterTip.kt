package com.avoqado.pos.core.data.network.models.transactions.summary

import com.google.gson.annotations.SerializedName

data class WaiterTip(
    @SerializedName("amount")
    val amount: Int?,
    @SerializedName("count")
    val count: Int?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("waiterId")
    val waiterId: String?,
)
