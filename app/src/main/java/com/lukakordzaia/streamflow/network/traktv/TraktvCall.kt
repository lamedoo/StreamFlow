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
                    response.code() == 400 -> {
                        AppConstants.TRAKTV_PENDING_AUTH
                    }
                    response.code() == 404 -> {
                        AppConstants.TRAKTV_NOT_FOUND
                    }
                    response.code() == 409 -> {
                        AppConstants.TRAKTV_CODE_USED
                    }
                    response.code() == 410 -> {
                        AppConstants.TRAKTV_CODE_EXPIRED
                    }
                    else -> {
                        AppConstants.TRAKTV_UNKNOWN_ERROR
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