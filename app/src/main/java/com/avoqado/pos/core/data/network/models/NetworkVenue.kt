package com.avoqado.pos.core.data.network.models

import com.google.gson.annotations.SerializedName

data class NetworkVenue(
    @SerializedName("address")
    val address: String?,
    @SerializedName("askNameOrdering")
    val askNameOrdering: Boolean?,
    @SerializedName("chainId")
    val chainId: String?,
    @SerializedName("city")
    val city: String?,
    @SerializedName("colorsId")
    val colorsId: String?,
    @SerializedName("configurationId")
    val configurationId: String?,
    @SerializedName("country")
    val country: String?,
    @SerializedName("createdAt")
    val createdAt: String?,
    @SerializedName("cuisine")
    val cuisine: String?,
    @SerializedName("dynamicMenu")
    val dynamicMenu: Boolean?,
    @SerializedName("email")
    val email: String?,
    @SerializedName("googleBusinessId")
    val googleBusinessId: String?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("image")
    val image: String?,
    @SerializedName("instagram")
    val instagram: String?,
    @SerializedName("language")
    val language: String?,
    @SerializedName("logo")
    val logo: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("paymentMethods")
    val paymentMethods: List<String?>?,
    @SerializedName("phone")
    val phone: String?,
    @SerializedName("posName")
    val posName: String?,
    @SerializedName("specialPayment")
    val specialPayment: Boolean?,
    @SerializedName("specialPaymentRef")
    val specialPaymentRef: String?,
    @SerializedName("stripeAccountId")
    val stripeAccountId: String?,
    @SerializedName("tables")
    val tables: List<NetworkTable?>?,
    @SerializedName("tipPercentage1")
    val tipPercentage1: String?,
    @SerializedName("tipPercentage2")
    val tipPercentage2: String?,
    @SerializedName("tipPercentage3")
    val tipPercentage3: String?,
    @SerializedName("tipPercentages")
    val tipPercentages: List<Double?>?,
    @SerializedName("type")
    val type: String?,
    @SerializedName("updatedAt")
    val updatedAt: String?,
    @SerializedName("utc")
    val utc: String?,
    @SerializedName("website")
    val website: String?,
    @SerializedName("wifiName")
    val wifiName: String?,
    @SerializedName("wifiPassword")
    val wifiPassword: String?,
    @SerializedName("waiters")
    val waiters: List<NetworkWaiter>?,
)
