package com.avoqado.pos.core.data.network

import com.avoqado.pos.core.data.network.models.NetworkBillDetail
import com.avoqado.pos.core.data.network.models.NetworkDetailTable
import com.avoqado.pos.core.data.network.models.NetworkShift
import com.avoqado.pos.core.data.network.models.NetworkSimpleTable
import com.avoqado.pos.core.data.network.models.NetworkVenue
import com.avoqado.pos.core.data.network.models.PasscodeBody
import com.avoqado.pos.core.data.network.models.ShiftBody
import com.avoqado.pos.core.data.network.models.TerminalMerchant
import com.avoqado.pos.core.data.network.models.WaiterData
import com.avoqado.pos.core.data.network.models.bills.NetworkBillDetailV2
import com.avoqado.pos.core.data.network.models.bills.NetworkBillV2
import com.avoqado.pos.core.data.network.models.transactions.NetworkDataShift
import com.avoqado.pos.core.data.network.models.transactions.NetworkShiftRecord
import com.avoqado.pos.core.data.network.models.transactions.payments.NetworkShiftPaymentsData
import com.avoqado.pos.core.data.network.models.transactions.summary.NetworkSummaryData
import com.google.gson.JsonObject
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface AvoqadoService {

    @GET("tpv/venues/{venueId}")
    suspend fun getVenueDetail(
        @Path("venueId") venueId: String
    ): NetworkVenue

    @GET("tpv/serial-number/{terminalCode}")
    suspend fun getTPV(
        @Path("terminalCode") terminalCode: String
    ) : TerminalMerchant

    @POST("tpv/venues/{venueId}/auth")
    suspend fun loginPasscode(
        @Path("venueId") venueId: String,
        @Body passcodeBody: PasscodeBody
    ): WaiterData

    @GET("tpv/venues/{venueId}/shift")
    suspend fun getRestaurantShift(
        @Path("venueId") venueId: String,
        @Query("pos_name") posName: String
    ): NetworkShiftRecord

    @POST("tpv/venues/{venueId}/shift")
    suspend fun registerRestaurantShift(
        @Path("venueId") venueId: String,
        @Body body: ShiftBody
    ) : NetworkShiftRecord

    @PATCH("tpv/venues/{venueId}/shift")
    suspend fun updateRestaurantShift(
        @Path("venueId") venueId: String,
        @Body body: ShiftBody
    ) : NetworkShiftRecord

    @GET("tpv/venues/{venueId}/shifts")
    suspend fun getShiftSummary(
        @Path("venueId") venueId: String,
        @Query("pageSize") pageSize: Int,
        @Query("pageNumber") pageNumber: Int,
        @Query("startTime", encoded = true) startTime: String?,
        @Query("endTime", encoded = true) endTime: String?,
        @Query("waiterId", encoded = true) waiterId: String?
    ): NetworkDataShift

    @GET("tpv/venues/{venueId}/shifts-summary")
    suspend fun getSummary(
        @Path("venueId") venueId: String,
        @Query("startTime", encoded = true) startTime: String?,
        @Query("endTime", encoded = true) endTime: String?,
        @Query("waiterId", encoded = true) waiterId: String?
    ): NetworkSummaryData

    @GET("tpv/venues/{venueId}/payments")
    suspend fun getPaymentsSummary(
        @Path("venueId") venueId: String,
        @Query("pageSize") pageSize: Int,
        @Query("pageNumber") pageNumber: Int,
        @Query("startTime", encoded = true) startTime: String?,
        @Query("endTime", encoded = true) endTime: String?,
        @Query("waiterId", encoded = true) waiterId: String?
    ): NetworkShiftPaymentsData

    @GET("tpv/venues/{venueId}/bills")
    suspend fun getActiveBills(
        @Path("venueId") venueId: String
    ) : List<NetworkBillV2>

    @GET("tpv/venues/{venueId}/bills/{billId}")
    suspend fun getBillDetail(
        @Path("venueId") venueId: String,
        @Path("billId") billId: String,
    ) : NetworkBillDetailV2

}