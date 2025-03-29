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
import com.avoqado.pos.core.data.network.models.transactions.NetworkDataShift
import com.avoqado.pos.core.data.network.models.transactions.NetworkShiftRecord
import com.google.gson.JsonObject
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface AvoqadoService {
    @GET("venues/listVenues")
    suspend fun getVenues(): List<NetworkVenue>

    @GET("tpv/venues/{venueId}")
    suspend fun getVenueDetail(
        @Path("venueId") venueId: String
    ): NetworkVenue

    @GET("venues/{venueId}/tables")
    suspend fun getVenueTables(@Path("venueId") venueId: String): List<NetworkSimpleTable>

    @GET("venues/{venueId}/tables/{tableNumber}")
    suspend fun getVenueTableDetail(
        @Path("venueId") venueId: String,
        @Path("tableNumber") tableNumber: String
    ): NetworkDetailTable

    @GET("venues/{venueId}/bills/{billId}")
    suspend fun getTableBill(
        @Path("venueId") venueId: String,
        @Path("billId") billId: String
    ): NetworkBillDetail

    @GET("{billURL}")
    suspend fun getTableBillByUrl(
        @Path("billURL") billURL: String
    ): NetworkBillDetail

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
        @Query("startTime") startTime: String?,
        @Query("endTime") endTime: String?,
        @Query("waiterId") waiterId: String?
    ): NetworkDataShift
}