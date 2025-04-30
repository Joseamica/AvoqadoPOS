package com.avoqado.pos.core.data.network

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object AvoqadoAPI {
    val retrofit: Retrofit by lazy {
        Retrofit
            .Builder()
            .baseUrl(ServerConfig.getApiBaseUrl())
            .client(
                OkHttpClient
                    .Builder()
                    .addInterceptor(
                        HttpLoggingInterceptor().apply {
                            level = HttpLoggingInterceptor.Level.BODY
                        },
                    ).connectTimeout(120, TimeUnit.SECONDS)
                    .readTimeout(120, TimeUnit.SECONDS)
                    .build(),
            ).addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val retrofitMenta: Retrofit by lazy {
        Retrofit
            .Builder()
            .baseUrl("https://api.menta.global/api/")
            .client(
                OkHttpClient
                    .Builder()
                    .addInterceptor(
                        HttpLoggingInterceptor().apply {
                            level = HttpLoggingInterceptor.Level.BODY
                        },
                    ).connectTimeout(120, TimeUnit.SECONDS)
                    .readTimeout(120, TimeUnit.SECONDS)
                    .addInterceptor {
                        it.proceed(
                            it.request().also {
                                Log.i("AvoqadoLogging", "Request: ${it.url} \n ${it.headers}")
                            },
                        )
                    }.build(),
            ).addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: AvoqadoService by lazy {
        retrofit.create(AvoqadoService::class.java)
    }

    val mentaService: MentaService by lazy {
        retrofitMenta.create(MentaService::class.java)
    }
}
