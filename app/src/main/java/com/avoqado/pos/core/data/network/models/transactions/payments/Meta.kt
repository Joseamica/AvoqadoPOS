package com.avoqado.pos.core.data.network.models.transactions.payments


import com.google.gson.annotations.SerializedName

data class Meta(
    @SerializedName("currentPage")
    val currentPage: Int?,
    @SerializedName("hasNextPage")
    val hasNextPage: Boolean?,
    @SerializedName("hasPrevPage")
    val hasPrevPage: Boolean?,
    @SerializedName("pageSize")
    val pageSize: Int?,
    @SerializedName("totalCount")
    val totalCount: Int?,
    @SerializedName("totalPages")
    val totalPages: Int?
)