package com.avoqado.pos.features.payment.data.network.models

import com.google.gson.annotations.SerializedName


//            {
//                //MENTA
//                "venueId": "madre_cafecito",
//                "amount": 100,
//                "tip": 10,
//                "status": "ACCEPTED",
//                "method": "CASH",
//                "source": "TPV",
//                "splitType": "CUSTOMAMOUNT",
//                "tpvId": "cm6rzjdwy0016gtnfoczma3rt",
//                "waiterName": "Raul Jimenez",
//                //MENTA
//                "cardBrand": "VISA",
//                "last4": "7777",
//                "typeOfCard": "CREDIT",
//                "currency": "MXN",
//                "bank": "BBVA",
//                "mentaAuthorizationReference": "180257875290",
//                "mentaOperationId": "6e5a7e93-6036-46c0-bf79-a57998d3d4e9",
//                "mentaTicketId": "536228666"
//            }

//            {
//                //CASH
//                "venueId": "madre_cafecito",
//                "amount": 100,
//                "tip": 10,
//                "status": "ACCEPTED",
//                "method": "CASH",
//                "source": "TPV",
//                "splitType": "CUSTOMAMOUNT",
//                "tpvId": "cm6rzjdwy0016gtnfoczma3rt",
//                "waiterName": "Raul Jimenez",
//            }

data class RecordPaymentBody(
    @SerializedName("venueId") val venueId: String,
    @SerializedName("amount") val amount: Int,
    @SerializedName("tip") val tip: Int,
    @SerializedName("status") val status: String,
    @SerializedName("method") val method: String,
    @SerializedName("source") val source: String,
    @SerializedName("splitType") val splitType: String,
    @SerializedName("tpvId") val tpvId: String,
    @SerializedName("waiterName") val waiterName: String,
    @SerializedName("cardBrand") val cardBrand: String? = null,
    @SerializedName("last4") val last4: String? = null,
    @SerializedName("typeOfCard") val typeOfCard: String? = null,
    @SerializedName("currency") val currency: String? = null,
    @SerializedName("bank") val bank: String? = null,
    @SerializedName("mentaAuthorizationReference") val mentaAuthorizationReference: String? = null,
    @SerializedName("mentaOperationId") val mentaOperationId: String? = null,
    @SerializedName("mentaTicketId") val mentaTicketId: String? = null
)