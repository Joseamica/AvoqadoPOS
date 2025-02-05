package com.avoqado.pos.core.data.network.models


import com.google.gson.annotations.SerializedName

data class RewardProduct(
    @SerializedName("createdAt")
    val createdAt: String?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("productKey")
    val productKey: String?,
    @SerializedName("productName")
    val productName: String?,
    @SerializedName("quantityNeeded")
    val quantityNeeded: Int?,
    @SerializedName("reward")
    val reward: String?,
    @SerializedName("type")
    val type: String?,
    @SerializedName("updatedAt")
    val updatedAt: String?,
    @SerializedName("userProductReward")
    val userProductReward: List<Any?>?,
    @SerializedName("venueId")
    val venueId: String?
)