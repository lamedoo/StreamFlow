package com.lukakordzaia.streamflow.network.traktv

import com.lukakordzaia.streamflow.datamodels.SendTraktvClientId
import com.lukakordzaia.streamflow.datamodels.TraktvDeviceCode
import com.lukakordzaia.streamflow.datamodels.TraktvGetToken
import com.lukakordzaia.streamflow.datamodels.TraktvRequestToken
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface TraktvNetwork {

    @Headers("Content-Type: application/json")
    @POST("https://api.trakt.tv/oauth/device/code")
    suspend fun getDeviceCode(@Body client_id: SendTraktvClientId) : Response<TraktvDeviceCode>

    @Headers("Content-Type: application/json")
    @POST("https://api.trakt.tv/oauth/device/token")
    suspend fun getUserToken(@Body tokenRequest: TraktvRequestToken) : Response<TraktvGetToken>
}