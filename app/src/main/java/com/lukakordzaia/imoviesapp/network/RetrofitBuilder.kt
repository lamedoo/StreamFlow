package com.lukakordzaia.imoviesapp.network

import com.lukakordzaia.imoviesapp.utils.AppConstants
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitBuilder(networkConnectionInterceptor: NetworkConnectionInterceptor) {
    private val okHttpClient = OkHttpClient()
            .newBuilder()
            .addInterceptor(networkConnectionInterceptor)
            .addInterceptor(getInterceptor())
            .build()

    private val retrofit: Retrofit = Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(AppConstants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    private fun getInterceptor(): Interceptor {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return loggingInterceptor
    }

    fun buildService(): TitlesNetwork {
        return retrofit.create(TitlesNetwork::class.java)
    }
}
