package com.avoqado.pos.data.network.models


import com.google.gson.annotations.SerializedName

data class NetworkDetailTable(
    @SerializedName("message")
    val message: String?,
    @SerializedName("redirect")
    val redirect: Boolean?,
    @SerializedName("rewardProducts")
    val rewardProducts: List<RewardProduct?>?,
    @SerializedName("table")
    val table: Table?,
    @SerializedName("url")
    val url: String?,
    @SerializedName("userLoggedIn")
    val userLoggedIn: Boolean?
)