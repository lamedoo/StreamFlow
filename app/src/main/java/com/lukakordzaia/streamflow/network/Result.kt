package com.lukakordzaia.streamflow.network

import java.io.IOException

sealed class Result<out T: Any> {
    data class Success<out T : Any>(val data: T) : Result<T>()
    data class Internet(val exception: String) : Result<Nothing>()
    data class Error(val exception: String) : Result<Nothing>()
}

data class LoadingState private constructor(val status: Status) {
    companion object {
        val LOADED = LoadingState(Status.SUCCESS)
        val LOADING = LoadingState(Status.RUNNING)
    }

    enum class Status {
        RUNNING,
        SUCCESS
    }
}

class InternetConnection(message: String): IOException(message)
