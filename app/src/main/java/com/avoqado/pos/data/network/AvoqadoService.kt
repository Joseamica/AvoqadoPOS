package com.avoqado.pos.data.network

import com.avoqado.pos.data.network.models.NetworkDetailTable
import com.avoqado.pos.data.network.models.NetworkSimpleTable
import com.avoqado.pos.data.network.models.NetworkVenue
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
}