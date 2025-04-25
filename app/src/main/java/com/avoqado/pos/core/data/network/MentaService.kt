package com.avoqado.pos.core.data.network

import com.avoqado.pos.core.data.network.models.PagedMerchants
import com.avoqado.pos.core.data.network.models.PagedTerminals
import retrofit2.http.GET
import retrofit2.http.Header

interface MentaService {
    @GET("v1/terminals?size=100")
    suspend fun getTerminals(
        @Header("Authorization") token: String,
    ): PagedTerminals

    @GET("v1/merchants?size=100")
    suspend fun getMerchants(
        @Header("Authorization") token: String,
    ): PagedMerchants
}
