package com.avoqado.pos.data.network.models


import com.google.gson.annotations.SerializedName

data class Table(
    @SerializedName("bill")
    val bill: NetworkBill?,
    @SerializedName("venue")
    val venue: NetworkVenue?
)