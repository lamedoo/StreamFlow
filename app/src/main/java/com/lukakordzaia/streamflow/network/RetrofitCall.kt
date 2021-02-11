package com.lukakordzaia.streamflow.network

import retrofit2.Response

open class RetrofitCall {
    suspend fun <T: Any> retrofitCall(call: suspend () -> Response<T>): Result<T> {
        return try {
            val response = call.invoke()
            var error = ""
            if (response.isSuccessful) {
                Result.Success (response.body()!!)
            } else {
                error = when {
                    response.code() == 404 -> {
                        "404 - არ იძებნება"
                    }
                    response.code() == 500 -> {
                        "ვერ ხერხდება სერვერთან დაკავშირება"
                    }
                    else -> {
                        "410 - გაურკვეველი პრობლემა"
                    }
                }
                Result.Error(error)
            }
        } catch (e: Exception) {
            when (e) {
                is InternetConnection -> {
                    Result.Internet(e.toString())
                }
                else -> {
                    Result.Error(e.toString())
                }
            }
        }
    }
}