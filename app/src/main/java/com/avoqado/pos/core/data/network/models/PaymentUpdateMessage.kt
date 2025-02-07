package com.avoqado.pos.core.data.network.models


import com.google.gson.annotations.SerializedName

data class PaymentUpdateMessage(
    @SerializedName("amount")
    val amount: String?,
    @SerializedName("anonymousUser")
    val anonymousUser: String?,
    @SerializedName("avoFee")
    val avoFee: String?,
    @SerializedName("bank")
    val bank: String?,
    @SerializedName("billId")
    val billId: String?,
    @SerializedName("cardBrand")
    val cardBrand: String?,
    @SerializedName("cardCountry")
    val cardCountry: String?,
    @SerializedName("createdAt")
    val createdAt: String?,
    @SerializedName("currency")
    val currency: String?,
    @SerializedName("customerId")
    val customerId: String?,
    @SerializedName("equalPartsPartySize")
    val equalPartsPartySize: String?,
    @SerializedName("equalPartsPayedFor")
    val equalPartsPayedFor: String?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("last4")
    val last4: String?,
    @SerializedName("mentaAuthorizationReference")
    val mentaAuthorizationReference: String?,
    @SerializedName("mentaOperationId")
    val mentaOperationId: String?,
    @SerializedName("mentaTicketId")
    val mentaTicketId: String?,
    @SerializedName("method")
    val method: String?,
    @SerializedName("methodString")
    val methodString: String?,
    @SerializedName("receiptUrl")
    val receiptUrl: String?,
    @SerializedName("source")
    val source: String?,
    @SerializedName("splitType")
    val splitType: String?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("stripePaymentIntentId")
    val stripePaymentIntentId: String?,
    @SerializedName("tableNumber")
    val tableNumber: Int?,
    @SerializedName("token")
    val token: String?,
    @SerializedName("tpvId")
    val tpvId: String?,
    @SerializedName("typeOfCard")
    val typeOfCard: String?,
    @SerializedName("updatedAt")
    val updatedAt: String?,
    @SerializedName("userFee")
    val userFee: String?,
    @SerializedName("userId")
    val userId: String?,
    @SerializedName("venueId")
    val venueId: String?,
    @SerializedName("waiterId")
    val waiterId: String?,
    @SerializedName("waiterName")
    val waiterName: String?
)