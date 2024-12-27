package com.avoqado.pos.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object AvoqadoAPI {
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://api.avoqado.io/v1")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: AvoqadoService by lazy {
        retrofit.create(AvoqadoService::class.java)
    }
}