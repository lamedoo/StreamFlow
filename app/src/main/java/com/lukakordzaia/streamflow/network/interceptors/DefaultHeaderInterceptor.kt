package com.lukakordzaia.streamflow.network.interceptors

import com.lukakordzaia.streamflow.sharedpreferences.AuthSharedPreferences
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class DefaultHeaderInterceptor(private val authSharedPreferences: AuthSharedPreferences) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request: Request = chain.request()

        request = request.newBuilder()
            .addHeader("User-Agent", "imovies")
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Bearer ${authSharedPreferences.getLoginToken()}" )
            .build()
        
        return chain.proceed(request)
    }
}