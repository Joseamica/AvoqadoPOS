package com.avoqado.pos.features.payment.data.network.models


import com.google.gson.annotations.SerializedName

data class TpvData(
    @SerializedName("configuration")
    val configuration: String?,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("customerId")
    val customerId: String?,
    @SerializedName("id")
    val id: String,
    @SerializedName("idMenta")
    val idMenta: String?,
    @SerializedName("model")
    val model: String?,
    @SerializedName("name")
    val name: String,
    @SerializedName("serial")
    val serial: String,
    @SerializedName("status")
    val status: String?,
    @SerializedName("tradeMark")
    val tradeMark: String?,
    @SerializedName("updatedAt")
    val updatedAt: String,
    @SerializedName("venueId")
    val venueId: String,
    @SerializedName("version")
    val version: String?
)