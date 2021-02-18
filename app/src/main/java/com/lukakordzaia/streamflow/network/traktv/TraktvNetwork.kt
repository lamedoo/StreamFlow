package com.lukakordzaia.streamflow.network.traktv

import com.lukakordzaia.streamflow.datamodels.*
import com.lukakordzaia.streamflow.sharedpreferences.AuthSharedPreferences
import com.lukakordzaia.streamflow.utils.AppConstants
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface TraktvNetwork {

    @Headers("Content-Type: application/json")
    @POST("https://api.trakt.tv/oauth/device/code")
    suspend fun getDeviceCode(@Body client_id: SendTraktvClientId) : Response<TraktvDeviceCode>

    @Headers("Content-Type: application/json")
    @POST("https://api.trakt.tv/oauth/device/token")
    suspend fun getUserToken(@Body tokenRequest: TraktvRequestToken) : Response<TraktvGetToken>

    @Headers(
            "Content-Type: application/json",
            "trakt-api-version: 2",
            "trakt-api-key: ${AppConstants.TRAKTV_CLIENT_ID}"
            )
    @POST("https://api.trakt.tv/users/me/lists")
    suspend fun createNewList(@Body newList: TraktvNewList, @Header("Authorization") accessToken: String) : Response<String>
}