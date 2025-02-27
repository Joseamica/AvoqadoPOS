package com.avoqado.pos.core.data.network.models


import com.google.gson.annotations.SerializedName

data class SettlementCondition(
    @SerializedName("cbu_or_cvu")
    val cbuOrCvu: String?,
    @SerializedName("settlement")
    val settlement: String?,
    @SerializedName("transaction_fee")
    val transactionFee: String?
)