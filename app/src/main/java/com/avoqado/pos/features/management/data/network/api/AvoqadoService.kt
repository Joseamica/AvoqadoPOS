package com.avoqado.pos.features.management.data.network.api

import com.avoqado.pos.data.network.models.NetworkBillDetail
import com.avoqado.pos.data.network.models.NetworkDetailTable
import com.avoqado.pos.data.network.models.NetworkSimpleTable
import com.avoqado.pos.data.network.models.NetworkVenue
import com.avoqado.pos.data.network.models.TerminalMerchant
import retrofit2.http.GET
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

    @GET("tpv/{terminalCode}")
    suspend fun getTPV(
        @Path("terminalCode") terminalCode: String
    ) : TerminalMerchant
}