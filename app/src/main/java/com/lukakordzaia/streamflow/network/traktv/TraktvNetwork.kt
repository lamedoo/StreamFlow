package com.lukakordzaia.streamflow.network.traktv

import com.lukakordzaia.streamflow.datamodels.*
import com.lukakordzaia.streamflow.utils.AppConstants
import retrofit2.Response
import retrofit2.http.*

interface TraktvNetwork {

    @Headers("Content-Type: application/json")
    @POST("https://api.trakt.tv/oauth/device/code")
    suspend fun getDeviceCode(@Body client_id: SendTraktvClientId) : Response<TraktvDeviceCode>

    @Headers("Content-Type: application/json")
    @POST("https://api.trakt.tv/oauth/device/token")
    suspend fun getUserToken(@Body tokenRequest: TraktRequestToken) : Response<TraktGetToken>

    @Headers(
            "Content-Type: application/json",
            "trakt-api-version: 2",
            "trakt-api-key: ${AppConstants.TRAKTV_CLIENT_ID}"
            )
    @POST("https://api.trakt.tv/users/me/lists")
    suspend fun createNewList(@Body newList: TraktNewList, @Header("Authorization") accessToken: String) : Response<String>

    @Headers(
            "Content-Type: application/json",
            "trakt-api-version: 2",
            "trakt-api-key: ${AppConstants.TRAKTV_CLIENT_ID}"
    )
    @GET("https://api.trakt.tv/users/me/lists/streamflow-list")
    suspend fun getStreamFlowList(@Header("Authorization") accessToken: String) : Response<TraktGetUserList>

    @Headers(
            "Content-Type: application/json",
            "trakt-api-version: 2",
            "trakt-api-key: ${AppConstants.TRAKTV_CLIENT_ID}"
    )
    @GET("https://api.trakt.tv/users/me")
    suspend fun getUserProfile(@Header("Authorization") accessToken: String) : Response<TraktUserProfile>

    @Headers(
            "Content-Type: application/json",
            "trakt-api-version: 2",
            "trakt-api-key: ${AppConstants.TRAKTV_CLIENT_ID}"
    )
    @POST("https://api.trakt.tv/users/me/lists/streamflow-list/items")
    suspend fun addMovieToList(@Body movieToTraktList: AddMovieToTraktList, @Header("Authorization") accessToken: String) : Response<AddToTraktListResponse>

    @Headers(
            "Content-Type: application/json",
            "trakt-api-version: 2",
            "trakt-api-key: ${AppConstants.TRAKTV_CLIENT_ID}"
    )
    @POST("https://api.trakt.tv/users/me/lists/streamflow-list/items")
    suspend fun addTvShowToList(@Body tvShowToTraktList: AddTvShowToTraktList, @Header("Authorization") accessToken: String) : Response<AddToTraktListResponse>

    @Headers(
            "Content-Type: application/json",
            "trakt-api-version: 2",
            "trakt-api-key: ${AppConstants.TRAKTV_CLIENT_ID}"
    )
    @POST("https://api.trakt.tv/users/me/lists/list_id/items/remove")
    suspend fun removeMovieFromList(@Body movieToTraktList: AddMovieToTraktList, @Header("Authorization") accessToken: String) : Response<RemoveFromTraktListResponse>

    @Headers(
            "Content-Type: application/json",
            "trakt-api-version: 2",
            "trakt-api-key: ${AppConstants.TRAKTV_CLIENT_ID}"
    )
    @GET("https://api.trakt.tv/users/me/lists/streamflow-list/items/{type}")
    suspend fun getStreamFlowListByType(@Path("type") type: String, @Header("Authorization") accessToken: String) : Response<GetTraktSfListByType>
}