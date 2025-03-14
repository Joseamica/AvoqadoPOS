package com.avoqado.pos.core.data.network

import com.avoqado.pos.core.data.network.models.NetworkBillDetail
import com.avoqado.pos.core.data.network.models.NetworkDetailTable
import com.avoqado.pos.core.data.network.models.NetworkSimpleTable
import com.avoqado.pos.core.data.network.models.NetworkVenue
import com.avoqado.pos.core.data.network.models.PasscodeBody
import com.avoqado.pos.core.data.network.models.TerminalMerchant
import com.avoqado.pos.core.data.network.models.WaiterData
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface AvoqadoService {
    @GET("venues/listVenues")
    suspend fun getVenues(): List<NetworkVenue>

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
}