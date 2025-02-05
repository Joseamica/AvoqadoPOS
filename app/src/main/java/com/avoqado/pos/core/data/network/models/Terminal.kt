package com.avoqado.pos.core.data.network.models


import com.google.gson.annotations.SerializedName

data class Terminal(
    @SerializedName("create_date")
    val createDate: String,
    @SerializedName("customer_id")
    val customerId: String,
    @SerializedName("delete_date")
    val deleteDate: String,
    @SerializedName("features")
    val features: List<String>,
    @SerializedName("hardware_version")
    val hardwareVersion: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("merchant_id")
    val merchantId: String,
    @SerializedName("model")
    val model: String,
    @SerializedName("serial_code")
    val serialCode: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("trade_mark")
    val tradeMark: String,
    @SerializedName("update_date")
    val updateDate: String
)