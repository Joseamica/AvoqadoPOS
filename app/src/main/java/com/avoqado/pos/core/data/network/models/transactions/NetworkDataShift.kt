package com.avoqado.pos.core.data.network.models.transactions

import com.google.gson.annotations.SerializedName

data class NetworkDataShift(
    @SerializedName("data")
    val data : List<NetworkShiftRecord>,
    @SerializedName("pagination")
    val pagination: Pagination
)
