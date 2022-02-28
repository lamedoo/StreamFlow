package com.lukakordzaia.core.network

import java.io.IOException

sealed class ResultData<out T : Any> {
    data class Success<out T : Any>(val data: T) : ResultData<T>()
    object Internet : ResultData<Nothing>()
    data class Error(val exception: String) : ResultData<Nothing>()
}

sealed class ResultDomain<out Success : Any, out Error : Any> {
    data class Success<out Success : Any>(val data: Success) : ResultDomain<Success, Nothing>()
    data class Error<out Error : Any>(val exception: Error) : ResultDomain<Nothing, Error>()
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

class InternetConnection : IOException()
