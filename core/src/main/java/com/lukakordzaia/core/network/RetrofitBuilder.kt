package com.lukakordzaia.core.network

import com.lukakordzaia.core.BuildConfig
import com.lukakordzaia.core.network.interceptors.DefaultHeaderInterceptor
import com.lukakordzaia.core.network.interceptors.NetworkConnectionInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitBuilder(private val networkConnectionInterceptor: NetworkConnectionInterceptor, private val defaultHeaderInterceptor: DefaultHeaderInterceptor) {
    private var retrofitInstance: Retrofit? = null

    fun getRetrofitInstance(): Retrofit {

        val okHttpClient = OkHttpClient()
            .newBuilder()
            .addInterceptor(defaultHeaderInterceptor)
            .addInterceptor(networkConnectionInterceptor)

        if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

            okHttpClient
                .addInterceptor(loggingInterceptor)
        }

        if (retrofitInstance == null) {
            retrofitInstance = Retrofit.Builder()
                .baseUrl(EndPoints.BASE_URL)
                .client(okHttpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofitInstance!!
    }
}
