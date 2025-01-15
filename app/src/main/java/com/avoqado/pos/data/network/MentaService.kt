package com.avoqado.pos.data.network

import com.avoqado.pos.data.network.models.PagedTerminals
import retrofit2.http.GET
import retrofit2.http.Header

interface MentaService {
    @GET("v1/terminals?size=100")
    suspend fun getTerminals(
        @Header("Authorization") token: String
    ): PagedTerminals
}