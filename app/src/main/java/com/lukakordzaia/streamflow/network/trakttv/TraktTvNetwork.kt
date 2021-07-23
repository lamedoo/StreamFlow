package com.lukakordzaia.streamflow.network.trakttv

import com.lukakordzaia.streamflow.network.EndPoints
import com.lukakordzaia.streamflow.network.models.trakttv.request.*
import com.lukakordzaia.streamflow.network.models.trakttv.response.*
import com.lukakordzaia.streamflow.utils.AppConstants
import retrofit2.Response
import retrofit2.http.*

interface TraktTvNetwork {

    @POST(EndPoints.DEVICE_CODE)
    suspend fun getDeviceCode(@Body client_id: GetDeviceCodeRequestBody) : Response<GetDeviceCodeResponse>

    @POST(EndPoints.USER_TOKEN)
    suspend fun getUserToken(@Body tokenRequestRequestBody: GetUserTokenRequestBody) : Response<GetUserTokenResponse>

    @Headers(
        "trakt-api-version: 2",
        "trakt-api-key: ${AppConstants.TRAKTV_CLIENT_ID}"
    )
    @GET(EndPoints.USER_PROFILE)
    suspend fun getUserProfile(@Header("Authorization") accessToken: String) : Response<GetUserProfileResponse>

    @Headers(
            "trakt-api-version: 2",
            "trakt-api-key: ${AppConstants.TRAKTV_CLIENT_ID}"
            )
    @POST(EndPoints.ADD_LIST)
    suspend fun addNewList(@Body newList: AddNewListRequestBody,
                           @Header("Authorization") accessToken: String) : Response<String>

    @Headers(
            "trakt-api-version: 2",
            "trakt-api-key: ${AppConstants.TRAKTV_CLIENT_ID}"
    )
    @GET(EndPoints.USER_LIST)
    suspend fun getUserList(@Header("Authorization") accessToken: String) : Response<GetUserListResponse>

    @Headers(
            "trakt-api-version: 2",
            "trakt-api-key: ${AppConstants.TRAKTV_CLIENT_ID}"
    )
    @POST(EndPoints.ADD_MOVIE_TO_LIST)
    suspend fun addMovieToList(@Body movieToTraktListRequestBody: AddMovieToTraktListRequestBody,
                               @Header("Authorization") accessToken: String) : Response<AddTitleToTraktListResponse>

    @Headers(
            "trakt-api-version: 2",
            "trakt-api-key: ${AppConstants.TRAKTV_CLIENT_ID}"
    )
    @POST(EndPoints.ADD_TV_SHOW_TO_LIST)
    suspend fun addTvShowToList(@Body tvShowToTraktListRequestBody: AddTvShowToTraktListRequestBody,
                                @Header("Authorization") accessToken: String) : Response<AddTitleToTraktListResponse>

    @Headers(
            "trakt-api-version: 2",
            "trakt-api-key: ${AppConstants.TRAKTV_CLIENT_ID}"
    )
    @POST(EndPoints.REMOVE_MOVIE_FROM_LIST)
    suspend fun removeMovieFromList(@Body movieToTraktListRequestBody: AddMovieToTraktListRequestBody,
                                    @Header("Authorization") accessToken: String) : Response<RemoveFromTraktListResponse>

    @Headers(
            "trakt-api-version: 2",
            "trakt-api-key: ${AppConstants.TRAKTV_CLIENT_ID}"
    )
    @POST(EndPoints.REMOVE_TV_SHOW_FROM_LIST)
    suspend fun removeTvShowFromList(@Body tvShowToTraktListRequestBody: AddTvShowToTraktListRequestBody,
                                     @Header("Authorization") accessToken: String) : Response<RemoveFromTraktListResponse>

    @Headers(
            "trakt-api-version: 2",
            "trakt-api-key: ${AppConstants.TRAKTV_CLIENT_ID}"
    )
    @GET(EndPoints.USER_LIST_BY_TYPE)
    suspend fun getListByTitleType(@Path("type") type: String,
                                   @Header("Authorization") accessToken: String) : Response<GetListByTitleTypeResponse>
}