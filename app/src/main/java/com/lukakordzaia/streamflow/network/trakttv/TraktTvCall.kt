package com.lukakordzaia.streamflow.network.trakttv

import com.lukakordzaia.streamflow.network.InternetConnection
import com.lukakordzaia.streamflow.network.Result
import com.lukakordzaia.streamflow.utils.AppConstants
import retrofit2.Response

open class TraktTvCall {
    suspend fun <T: Any> traktvCall(call: suspend () -> Response<T>): Result<T> {
        return try {
            val response = call.invoke()
            var error = ""
            if (response.isSuccessful) {
                Result.Success(response.body()!!)
            } else {
                error = when {
                    response.code() == 400 -> {
                        AppConstants.TRAKT_PENDING_AUTH
                    }
                    response.code() == 401 -> {
                        "401"
                    }
                    response.code() == 404 -> {
                        AppConstants.TRAKT_NOT_FOUND
                    }
                    response.code() == 409 -> {
                        AppConstants.TRAKT_CODE_USED
                    }
                    response.code() == 410 -> {
                        AppConstants.TRAKT_CODE_EXPIRED
                    }
                    else -> {
                        AppConstants.TRAKT_UNKNOWN_ERROR
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