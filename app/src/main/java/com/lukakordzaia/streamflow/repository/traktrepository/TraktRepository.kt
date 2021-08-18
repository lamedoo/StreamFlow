package com.lukakordzaia.streamflow.repository.traktrepository

import com.lukakordzaia.streamflow.network.Result
import com.lukakordzaia.streamflow.network.models.trakttv.request.AddMovieToTraktListRequestBody
import com.lukakordzaia.streamflow.network.models.trakttv.request.AddNewListRequestBody
import com.lukakordzaia.streamflow.network.models.trakttv.request.AddTvShowToTraktListRequestBody
import com.lukakordzaia.streamflow.network.models.trakttv.request.GetUserTokenRequestBody
import com.lukakordzaia.streamflow.network.models.trakttv.response.*

interface TraktRepository {
    suspend fun getDeviceCode(): Result<GetDeviceCodeResponse>
    suspend fun getUserToken(tokenRequestRequestBody: GetUserTokenRequestBody): Result<GetUserTokenResponse>
    suspend fun createNewList(newList: AddNewListRequestBody, accessToken: String): Result<String>
    suspend fun getSfList(accessToken: String): Result<GetUserListResponse>
    suspend fun getUserProfile(accessToken: String): Result<GetUserProfileResponse>
    suspend fun addMovieToTraktList(movieToTraktListRequestBody: AddMovieToTraktListRequestBody, accessToken: String): Result<AddTitleToTraktListResponse>
    suspend fun addTvShowToTraktList(tvShowToTraktListRequestBody: AddTvShowToTraktListRequestBody, accessToken: String): Result<AddTitleToTraktListResponse>
    suspend fun getSfListByType(type: String, accessToken: String): Result<GetListByTitleTypeResponse>
    suspend fun removeMovieFromTraktList(movieFromTraktListRequestBody: AddMovieToTraktListRequestBody, accessToken: String): Result<RemoveFromTraktListResponse>
    suspend fun removeTvShowFromTraktList(tvShowToTraktListRequestBody: AddTvShowToTraktListRequestBody, accessToken: String): Result<RemoveFromTraktListResponse>
}