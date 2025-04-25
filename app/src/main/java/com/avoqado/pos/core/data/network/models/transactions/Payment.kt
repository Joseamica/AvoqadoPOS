package com.avoqado.pos.core.data.network.models.transactions

import com.google.gson.annotations.SerializedName

data class Payment(
    @SerializedName("waiter")
    val waiter: String?,
)
