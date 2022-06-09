package com.lukakordzaia.core.network.github

import com.lukakordzaia.core.BuildConfig
import com.lukakordzaia.core.network.EndPoints
import com.lukakordzaia.core.network.interceptors.DefaultGithubHeaderInterceptor
import com.lukakordzaia.core.network.interceptors.NetworkConnectionInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitGithubBuilder(private val networkConnectionInterceptor: NetworkConnectionInterceptor, private val defaultHeaderInterceptor: DefaultGithubHeaderInterceptor) {
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
                .baseUrl(EndPoints.GITHUB_BASE_URL)
                .client(okHttpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofitInstance!!
    }
}
