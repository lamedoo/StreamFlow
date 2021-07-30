package com.lukakordzaia.streamflow.repository

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.lukakordzaia.streamflow.network.Result
import com.lukakordzaia.streamflow.network.models.trakttv.request.*
import com.lukakordzaia.streamflow.network.models.trakttv.response.*
import com.lukakordzaia.streamflow.network.trakttv.TraktTvCall
import com.lukakordzaia.streamflow.network.trakttv.TraktTvNetwork
import com.lukakordzaia.streamflow.utils.AppConstants
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class TraktRepository(private val service: TraktTvNetwork): TraktTvCall() {
    suspend fun getDeviceCode(): Result<GetDeviceCodeResponse> {
        return traktvCall { service.getDeviceCode(GetDeviceCodeRequestBody(AppConstants.TRAKTV_CLIENT_ID)) }
    }

    suspend fun getUserToken(tokenRequestRequestBody: GetUserTokenRequestBody): Result<GetUserTokenResponse> {
        return traktvCall { service.getUserToken(tokenRequestRequestBody) }
    }

    suspend fun createNewList(newList: AddNewListRequestBody, accessToken: String): Result<String> {
        return traktvCall { service.addNewList(newList, accessToken) }
    }

    suspend fun getSfList(accessToken: String): Result<GetUserListResponse> {
        return traktvCall { service.getUserList(accessToken) }
    }

    suspend fun getUserProfile(accessToken: String): Result<GetUserProfileResponse> {
        return traktvCall { service.getUserProfile(accessToken) }
    }

    suspend fun addMovieToTraktList(movieToTraktListRequestBody: AddMovieToTraktListRequestBody, accessToken: String): Result<AddTitleToTraktListResponse> {
        return traktvCall { service.addMovieToList(movieToTraktListRequestBody, accessToken) }
    }

    suspend fun addTvShowToTraktList(tvShowToTraktListRequestBody: AddTvShowToTraktListRequestBody, accessToken: String): Result<AddTitleToTraktListResponse> {
        return traktvCall { service.addTvShowToList(tvShowToTraktListRequestBody, accessToken) }
    }

    suspend fun getSfListByType(type: String, accessToken: String): Result<GetListByTitleTypeResponse> {
        return traktvCall { service.getListByTitleType(type, accessToken) }
    }

    suspend fun removeMovieFromTraktList(movieFromTraktListRequestBody: AddMovieToTraktListRequestBody, accessToken: String): Result<RemoveFromTraktListResponse> {
        return traktvCall { service.removeMovieFromList(movieFromTraktListRequestBody, accessToken) }
    }

    suspend fun removeTvShowFromTraktList(tvShowToTraktListRequestBody: AddTvShowToTraktListRequestBody, accessToken: String): Result<RemoveFromTraktListResponse> {
        return traktvCall { service.removeTvShowFromList(tvShowToTraktListRequestBody, accessToken) }
    }
}