package com.avoqado.pos.data.network

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object AvoqadoAPI {
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://api.avoqado.io/v1/")
        .client(
            OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                })
                .build()
        )
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val retrofitMenta: Retrofit = Retrofit.Builder()
        .baseUrl("https://api.menta.global/api/")
        .client(
            OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                })
                .addInterceptor {
                    it.proceed(
                        it.request().also {
                            Log.i("AvoqadoLogging", "Request: ${it.url} \n ${it.headers}")
                        }
                    )
                }
                .build()
        )
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: AvoqadoService by lazy {
        retrofit.create(AvoqadoService::class.java)
    }

    val mentaService: MentaService by lazy {
        retrofitMenta.create(MentaService::class.java)
    }
}