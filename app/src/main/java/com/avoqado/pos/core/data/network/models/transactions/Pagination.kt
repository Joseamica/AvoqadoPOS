package com.avoqado.pos.core.data.network.models.transactions

import com.google.gson.annotations.SerializedName

data class Pagination(
    @SerializedName("currentPage")
    val currentPage: Int?,
    @SerializedName("hasNextPage")
    val hasNextPage: Boolean?,
    @SerializedName("hasPreviousPage")
    val hasPreviousPage: Boolean?,
    @SerializedName("pageSize")
    val pageSize: Int?,
    @SerializedName("totalCount")
    val totalCount: Int?,
    @SerializedName("totalPages")
    val totalPages: Int?,
)
