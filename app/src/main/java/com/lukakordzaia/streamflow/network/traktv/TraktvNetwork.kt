package com.lukakordzaia.streamflow.network.traktv

import android.telecom.Call
import com.lukakordzaia.streamflow.datamodels.SendTraktvClientId
import com.lukakordzaia.streamflow.datamodels.TraktvDeviceCode
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface TraktvNetwork {

    @Headers("Content-Type: application/json")
    @POST("https://api.trakt.tv/oauth/device/code")
    suspend fun getDeviceCode(@Body client_id: SendTraktvClientId) : Response<TraktvDeviceCode>
}