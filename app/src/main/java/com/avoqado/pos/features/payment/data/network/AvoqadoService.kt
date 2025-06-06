package com.avoqado.pos.features.payment.data.network

import com.avoqado.pos.features.payment.data.network.models.PaymentRecordResponse
import com.avoqado.pos.features.payment.data.network.models.RecordPaymentBody
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface AvoqadoService {
    @POST("tpv/venues/{venueId}/bills/{tableNumber}")
    suspend fun recordPayment(
        @Path("venueId") venueId: String,
        @Path("tableNumber") tableNumber: String,
        @Body recordPaymentBody: RecordPaymentBody,
    ): PaymentRecordResponse
    
    @POST("tpv/venues/{venueId}/fast")
    suspend fun recordFastPayment(
        @Path("venueId") venueId: String,
        @Body recordPaymentBody: RecordPaymentBody,
    ): PaymentRecordResponse
}
