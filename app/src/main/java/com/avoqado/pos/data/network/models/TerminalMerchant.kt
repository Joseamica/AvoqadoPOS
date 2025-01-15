package com.avoqado.pos.data.network.models

import com.google.gson.annotations.SerializedName

data class TerminalMerchant(
    @SerializedName("venueId")
    val venueId: String?
)
