package com.lukakordzaia.imoviesapp.network

import android.util.Log
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceBuilder {
    private const val URL ="https://api.imovies.cc/api/v1/"

    private val okHttpClient = OkHttpClient()
        .newBuilder()
        .addInterceptor(getInterceptor())
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl(URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    suspend fun <T: Any> retrofitCall(call: suspend () -> Response<T>): Result<T> {
            return try {
                val response = call.invoke()
                if (response.isSuccessful) {
                    Result.Success (response.body()!!)
                } else {
                    if (response.code() == 403) {
                        Log.i("responsecode", "ავტორიზაცია არ განხორციელდა")
                    }
                    Result.Error(response.errorBody()?.string() ?: "Something goes wrong")
                }
            } catch (e: Exception) {
                Result.Error(e.message ?: "Internet error runs")
            }
    }

    private fun getInterceptor(): Interceptor {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return loggingInterceptor
    }

    fun <T> buildService (serviceType :Class<T>):T{
        return retrofit.create(serviceType)
    }
}
