package com.lukakordzaia.core.network.imovies

import com.lukakordzaia.core.utils.AppConstants
import com.lukakordzaia.core.network.InternetConnection
import com.lukakordzaia.core.network.ResultData
import retrofit2.Response

open class ImoviesCall {
    suspend fun <T : Any> imoviesCall(call: suspend () -> Response<T>): ResultData<T> {
        return try {
            val response = call.invoke()
            val error: String
            if (response.isSuccessful) {
                ResultData.Success(response.body()!!)
            } else {
                error = when {
                    response.code() == 404 -> {
                        AppConstants.NOT_FOUND_ERROR
                    }
                    response.code() == 500 -> {
                        AppConstants.SERVER_ERROR
                    }
                    response.code() == 502 -> {
                        AppConstants.TIME_OUT_ERROR
                    }
                    else -> {
                        AppConstants.UNKNOWN_ERROR
                    }
                }
                ResultData.Error(error)
            }
        } catch (e: Exception) {
            when (e) {
                is InternetConnection -> {
                    ResultData.Internet
                }
                else -> {
                    ResultData.Error(e.toString())
                }
            }
        }
    }
}