package com.lukakordzaia.core.network

import java.io.IOException
import java.lang.Error

sealed class ResultData<out T : Any> {
    data class Success<out T : Any>(val data: T) : ResultData<T>()
    data class Internet(val exception: String) : ResultData<Nothing>()
    data class Error(val exception: String) : ResultData<Nothing>()
}

sealed class ResultDomain<out Success : Any, out Error : String> {
    data class Success<out Success : Any>(val data: Success) : ResultDomain<Success, Nothing>()
    data class Internet<out Error : String>(val exception: Error) : ResultDomain<Nothing, Error>()
    data class Error<out Error : String>(val exception: Error) : ResultDomain<Nothing, Error>()
}

data class LoadingState(val status: Status) {
    companion object {
        val LOADED = LoadingState(Status.SUCCESS)
        val LOADING = LoadingState(Status.RUNNING)
        val ERROR = LoadingState(Status.ERROR)
    }

    enum class Status {
        RUNNING,
        SUCCESS,
        ERROR
    }
}

class InternetConnection(message: String): IOException(message)
