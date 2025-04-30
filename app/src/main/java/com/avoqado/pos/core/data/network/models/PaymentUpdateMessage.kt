package com.avoqado.pos.core.data.network.models

import com.google.gson.annotations.SerializedName

data class PaymentUpdateMessage(
    @SerializedName("amount")
    val amount: String? = null,
    @SerializedName("anonymousUser")
    val anonymousUser: String? = null,
    @SerializedName("avoFee")
    val avoFee: String? = null,
    @SerializedName("bank")
    val bank: String? = null,
    @SerializedName("billId")
    val billId: String? = null,
    @SerializedName("cardBrand")
    val cardBrand: String? = null,
    @SerializedName("cardCountry")
    val cardCountry: String? = null,
    @SerializedName("createdAt")
    val createdAt: String? = null,
    @SerializedName("currency")
    val currency: String? = null,
    @SerializedName("customerId")
    val customerId: String? = null,
    @SerializedName("equalPartsPartySize")
    val equalPartsPartySize: String? = null,
    @SerializedName("equalPartsPayedFor")
    val equalPartsPayedFor: String? = null,
    @SerializedName("id")
    val id: String? = null,
    @SerializedName("last4")
    val last4: String? = null,
    @SerializedName("mentaAuthorizationReference")
    val mentaAuthorizationReference: String? = null,
    @SerializedName("mentaOperationId")
    val mentaOperationId: String? = null,
    @SerializedName("mentaTicketId")
    val mentaTicketId: String? = null,
    @SerializedName("method")
    val method: String? = null,
    @SerializedName("methodString")
    val methodString: String? = null,
    @SerializedName("receiptUrl")
    val receiptUrl: String? = null,
    @SerializedName("source")
    val source: String? = null,
    @SerializedName("splitType")
    val splitType: String? = null,
    @SerializedName("status")
    val status: String? = null,
    @SerializedName("stripePaymentIntentId")
    val stripePaymentIntentId: String? = null,
    @SerializedName("tableNumber")
    val tableNumber: Int? = null,
    @SerializedName("token")
    val token: String? = null,
    @SerializedName("tpvId")
    val tpvId: String? = null,
    @SerializedName("typeOfCard")
    val typeOfCard: String? = null,
    @SerializedName("updatedAt")
    val updatedAt: String? = null,
    @SerializedName("userFee")
    val userFee: String? = null,
    @SerializedName("userId")
    val userId: String? = null,
    @SerializedName("venueId")
    val venueId: String? = null,
    @SerializedName("waiterId")
    val waiterId: String? = null,
    @SerializedName("waiterName")
    val waiterName: String? = null,
)
