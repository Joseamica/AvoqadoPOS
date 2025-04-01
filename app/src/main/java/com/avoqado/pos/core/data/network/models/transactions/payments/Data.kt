package com.avoqado.pos.core.data.network.models.transactions.payments


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("amount")
    val amount: String?,
    @SerializedName("anonymousUser")
    val anonymousUser: Any?,
    @SerializedName("avoFee")
    val avoFee: Any?,
    @SerializedName("bank")
    val bank: String?,
    @SerializedName("bill")
    val bill: Bill?,
    @SerializedName("billId")
    val billId: String?,
    @SerializedName("billV2")
    val billV2: Any?,
    @SerializedName("billV2Id")
    val billV2Id: Any?,
    @SerializedName("cardBrand")
    val cardBrand: String?,
    @SerializedName("cardCountry")
    val cardCountry: Any?,
    @SerializedName("createdAt")
    val createdAt: String?,
    @SerializedName("currency")
    val currency: String?,
    @SerializedName("customerId")
    val customerId: Any?,
    @SerializedName("equalPartsPartySize")
    val equalPartsPartySize: Any?,
    @SerializedName("equalPartsPayedFor")
    val equalPartsPayedFor: Any?,
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
    val method: Any?,
    @SerializedName("methodString")
    val methodString: Any?,
    @SerializedName("receiptUrl")
    val receiptUrl: Any?,
    @SerializedName("shiftId")
    val shiftId: String?,
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
    @SerializedName("tips")
    val tips: List<Tip?>?,
    @SerializedName("token")
    val token: String?,
    @SerializedName("tpv")
    val tpv: Tpv?,
    @SerializedName("tpvId")
    val tpvId: String?,
    @SerializedName("typeOfCard")
    val typeOfCard: String?,
    @SerializedName("updatedAt")
    val updatedAt: String?,
    @SerializedName("userFee")
    val userFee: Any?,
    @SerializedName("userId")
    val userId: Any?,
    @SerializedName("venueId")
    val venueId: String?,
    @SerializedName("waiter")
    val waiter: Waiter?,
    @SerializedName("waiterId")
    val waiterId: String?,
    @SerializedName("waiterName")
    val waiterName: String?
)