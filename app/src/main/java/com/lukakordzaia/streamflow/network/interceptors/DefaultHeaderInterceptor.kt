package com.lukakordzaia.streamflow.network.interceptors

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class DefaultHeaderInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request: Request = chain.request()

        request = request.newBuilder()
            .addHeader("User-Agent", "imovies")
            .build()
        
        return chain.proceed(request)
    }
}