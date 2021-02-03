package com.lukakordzaia.imoviesapp.network

import retrofit2.Response

open class RetrofitCall {
    suspend fun <T: Any> retrofitCall(call: suspend () -> Response<T>): Result<T> {
        return try {
            val response = call.invoke()
            if (response.isSuccessful) {
                Result.Success (response.body()!!)
            } else {
                Result.Error(response.errorBody().toString())
            }
        } catch (e: Exception) {
            Result.Internet(false)
        }
    }
}