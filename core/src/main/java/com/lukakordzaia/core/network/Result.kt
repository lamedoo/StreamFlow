package com.lukakordzaia.core.network

import java.io.IOException

sealed class Result<out T: Any> {
    data class Success<out T : Any>(val data: T) : Result<T>()
    data class Internet(val exception: String) : Result<Nothing>()
    data class Error(val exception: String) : Result<Nothing>()
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
