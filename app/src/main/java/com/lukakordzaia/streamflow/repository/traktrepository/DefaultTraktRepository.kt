package com.lukakordzaia.streamflow.repository.traktrepository

import com.lukakordzaia.streamflow.network.Result
import com.lukakordzaia.streamflow.network.models.trakttv.request.*
import com.lukakordzaia.streamflow.network.models.trakttv.response.*
import com.lukakordzaia.streamflow.network.trakttv.TraktTvCall
import com.lukakordzaia.streamflow.network.trakttv.TraktTvNetwork
import com.lukakordzaia.streamflow.utils.AppConstants

class DefaultTraktRepository(private val service: TraktTvNetwork): TraktTvCall(), TraktRepository {
    override suspend fun getDeviceCode(): Result<GetDeviceCodeResponse> {
        return traktvCall { service.getDeviceCode(GetDeviceCodeRequestBody(AppConstants.TRAKTV_CLIENT_ID)) }
    }

    override suspend fun getUserToken(tokenRequestRequestBody: GetUserTokenRequestBody): Result<GetUserTokenResponse> {
        return traktvCall { service.getUserToken(tokenRequestRequestBody) }
    }

    override suspend fun createNewList(newList: AddNewListRequestBody, accessToken: String): Result<String> {
        return traktvCall { service.addNewList(newList, accessToken) }
    }

    override suspend fun getSfList(accessToken: String): Result<GetUserListResponse> {
        return traktvCall { service.getUserList(accessToken) }
    }

    override suspend fun getUserProfile(accessToken: String): Result<GetUserProfileResponse> {
        return traktvCall { service.getUserProfile(accessToken) }
    }

    override suspend fun addMovieToTraktList(movieToTraktListRequestBody: AddMovieToTraktListRequestBody, accessToken: String): Result<AddTitleToTraktListResponse> {
        return traktvCall { service.addMovieToList(movieToTraktListRequestBody, accessToken) }
    }

    override suspend fun addTvShowToTraktList(tvShowToTraktListRequestBody: AddTvShowToTraktListRequestBody, accessToken: String): Result<AddTitleToTraktListResponse> {
        return traktvCall { service.addTvShowToList(tvShowToTraktListRequestBody, accessToken) }
    }

    override suspend fun getSfListByType(type: String, accessToken: String): Result<GetListByTitleTypeResponse> {
        return traktvCall { service.getListByTitleType(type, accessToken) }
    }

    override suspend fun removeMovieFromTraktList(movieFromTraktListRequestBody: AddMovieToTraktListRequestBody, accessToken: String): Result<RemoveFromTraktListResponse> {
        return traktvCall { service.removeMovieFromList(movieFromTraktListRequestBody, accessToken) }
    }

    override suspend fun removeTvShowFromTraktList(tvShowToTraktListRequestBody: AddTvShowToTraktListRequestBody, accessToken: String): Result<RemoveFromTraktListResponse> {
        return traktvCall { service.removeTvShowFromList(tvShowToTraktListRequestBody, accessToken) }
    }
}