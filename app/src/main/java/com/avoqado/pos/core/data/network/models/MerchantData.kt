package com.avoqado.pos.core.data.network.models

import com.google.gson.annotations.SerializedName

data class MerchantData(
    @SerializedName("activity")
    val activity: String?,
    @SerializedName("address")
    val address: Address?,
    @SerializedName("business_name")
    val businessName: String?,
    @SerializedName("category")
    val category: String?,
    @SerializedName("country")
    val country: String?,
    @SerializedName("create_date")
    val createDate: String?,
    @SerializedName("customer_id")
    val customerId: String?,
    @SerializedName("email")
    val email: String?,
    @SerializedName("fantasy_name")
    val fantasyName: String?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("legal_type")
    val legalType: String?,
    @SerializedName("merchant_code")
    val merchantCode: String?,
    @SerializedName("phone")
    val phone: String?,
    @SerializedName("representative")
    val representative: Representative?,
    @SerializedName("settlement_condition")
    val settlementCondition: SettlementCondition?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("tax")
    val tax: Tax?,
    @SerializedName("update_date")
    val updateDate: String?,
)
