package com.lukakordzaia.streamflow.network.traktv

import com.lukakordzaia.streamflow.network.InternetConnection
import com.lukakordzaia.streamflow.network.Result
import com.lukakordzaia.streamflow.utils.AppConstants
import retrofit2.Response

open class TraktvCall {
    suspend fun <T: Any> traktvCall(call: suspend () -> Response<T>): Result<T> {
        return try {
            val response = call.invoke()
            var error = ""
            if (response.isSuccessful) {
                Result.Success(response.body()!!)
            } else {
                error = when {
                    response.code() == 404 -> {
                        AppConstants.NOT_FOUND_ERROR
                    }
                    response.code() == 500 -> {
                        AppConstants.SERVER_ERROR
                    }
                    else -> {
                        AppConstants.UNKNOWN_ERROR
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