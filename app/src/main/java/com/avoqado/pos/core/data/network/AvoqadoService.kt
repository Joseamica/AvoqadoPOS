package com.avoqado.pos.core.data.network

import com.avoqado.pos.core.data.network.models.CreateBillRequest
import com.avoqado.pos.core.data.network.models.NetworkVenue
import com.avoqado.pos.core.data.network.models.PasscodeBody
import com.avoqado.pos.core.data.network.models.ShiftBody
import retrofit2.Response
import com.avoqado.pos.core.data.network.models.TerminalMerchant
import com.avoqado.pos.core.data.network.models.WaiterData
import com.avoqado.pos.core.data.network.models.bills.NetworkBillDetailV2
import com.avoqado.pos.core.data.network.models.bills.NetworkBillV2
import com.avoqado.pos.core.data.network.models.transactions.NetworkDataShift
import com.avoqado.pos.core.data.network.models.transactions.NetworkShiftRecord
import com.avoqado.pos.core.data.network.models.transactions.payments.NetworkPaymentsData
import com.avoqado.pos.core.data.network.models.transactions.summary.NetworkSummaryData
import com.avoqado.pos.features.menu.data.network.models.AvoqadoMenuResponse
import com.avoqado.pos.features.menu.data.network.models.NetworkModifierGroupResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface AvoqadoService {
    @GET("tpv/venues/{venueId}")
    suspend fun getVenueDetail(
        @Path("venueId") venueId: String,
    ): NetworkVenue

    @GET("tpv/serial-number/{terminalCode}")
    suspend fun getTPV(
        @Path("terminalCode") terminalCode: String,
    ): TerminalMerchant

    @POST("tpv/venues/{venueId}/auth")
    suspend fun loginPasscode(
        @Path("venueId") venueId: String,
        @Body passcodeBody: PasscodeBody,
    ): WaiterData

    @GET("tpv/venues/{venueId}/shift")
    suspend fun getRestaurantShift(
        @Path("venueId") venueId: String,
        @Query("pos_name") posName: String,
    ): NetworkShiftRecord

    @POST("tpv/venues/{venueId}/shift")
    suspend fun registerRestaurantShift(
        @Path("venueId") venueId: String,
        @Body body: ShiftBody,
    ): NetworkShiftRecord

    @PATCH("tpv/venues/{venueId}/shift")
    suspend fun updateRestaurantShift(
        @Path("venueId") venueId: String,
        @Body body: ShiftBody,
    ): NetworkShiftRecord

    @GET("tpv/venues/{venueId}/shifts")
    suspend fun getShiftSummary(
        @Path("venueId") venueId: String,
        @Query("pageSize") pageSize: Int,
        @Query("pageNumber") pageNumber: Int,
        @Query("startTime", encoded = true) startTime: String?,
        @Query("endTime", encoded = true) endTime: String?,
        @Query("waiterId", encoded = true) waiterId: String?,
    ): NetworkDataShift

    @GET("tpv/venues/{venueId}/shifts-summary")
    suspend fun getSummary(
        @Path("venueId") venueId: String,
        @Query("startTime", encoded = true) startTime: String?,
        @Query("endTime", encoded = true) endTime: String?,
        @Query("waiterId", encoded = true) waiterId: String?,
    ): NetworkSummaryData

    @GET("tpv/venues/{venueId}/payments")
    suspend fun getPayments(
        @Path("venueId") venueId: String,
        @Query("pageSize") pageSize: Int,
        @Query("pageNumber") pageNumber: Int,
        @Query("startTime", encoded = true) startTime: String?,
        @Query("endTime", encoded = true) endTime: String?,
        @Query("waiterId", encoded = true) waiterId: String?,
        @Query("paymentId", encoded = true) paymentId: String?,
    ): NetworkPaymentsData

    @GET("tpv/venues/{venueId}/bills")
    suspend fun getActiveBills(
        @Path("venueId") venueId: String,
    ): List<NetworkBillV2>

    @GET("tpv/venues/{venueId}/bills/{billId}")
    suspend fun getBillDetail(
        @Path("venueId") venueId: String,
        @Path("billId") billId: String,
    ): NetworkBillDetailV2
    
    @GET("tpv/venues/{venueId}/avoqado-menus")
    suspend fun getAvoqadoMenus(
        @Path("venueId") venueId: String,
    ): AvoqadoMenuResponse
    
    @GET("tpv/venues/{venueId}/products/{productId}/modifiers")
    suspend fun getProductModifiers(
        @Path("venueId") venueId: String,
        @Path("productId") productId: String
    ): NetworkModifierGroupResponse
    
    /**
     * Create a new bill (account) with a custom name
     * 
     * @param venueId The ID of the venue where to create the bill
     * @param requestBody Structured request with bill details
     * @return The created bill details
     */
    @POST("tpv/venues/{venueId}/bills")
    suspend fun createBill(
        @Path("venueId") venueId: String,
        @Body requestBody: CreateBillRequest
    ): Response<NetworkBillV2>
}
