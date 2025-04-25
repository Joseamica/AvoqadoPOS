package com.avoqado.pos.core.data.network.models

import com.google.gson.annotations.SerializedName

data class TerminalMerchant(
    @SerializedName("venueId")
    val venueId: String?,
)
