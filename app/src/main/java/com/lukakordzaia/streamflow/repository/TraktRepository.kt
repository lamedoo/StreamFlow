package com.lukakordzaia.streamflow.repository

import android.util.Log
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.lukakordzaia.streamflow.datamodels.*
import com.lukakordzaia.streamflow.network.Result
import com.lukakordzaia.streamflow.network.RetrofitBuilder
import com.lukakordzaia.streamflow.network.imovies.ImoviesNetwork
import com.lukakordzaia.streamflow.network.traktv.TraktvCall
import com.lukakordzaia.streamflow.network.traktv.TraktvNetwork
import com.lukakordzaia.streamflow.utils.AppConstants
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class TraktRepository(private val service: TraktvNetwork): TraktvCall() {
    suspend fun getDeviceCode(): Result<TraktvDeviceCode> {
        return traktvCall { service.getDeviceCode(SendTraktvClientId(AppConstants.TRAKTV_CLIENT_ID)) }
    }

    suspend fun getUserToken(tokenRequest: TraktRequestToken): Result<TraktGetToken> {
        return traktvCall { service.getUserToken(tokenRequest) }
    }

    suspend fun createNewList(newList: TraktNewList, accessToken: String): Result<String> {
        return traktvCall { service.createNewList(newList, accessToken) }
    }

    suspend fun getSfList(accessToken: String): Result<TraktGetUserList> {
        return traktvCall { service.getStreamFlowList(accessToken) }
    }

    suspend fun getUserProfile(accessToken: String): Result<TraktUserProfile> {
        return traktvCall { service.getUserProfile(accessToken) }
    }

    suspend fun addMovieToTraktList(movieToTraktList: AddMovieToTraktList, accessToken: String): Result<AddToTraktListResponse> {
        return traktvCall { service.addMovieToList(movieToTraktList, accessToken) }
    }

    suspend fun addTvShowToTraktList(tvShowToTraktList: AddTvShowToTraktList, accessToken: String): Result<AddToTraktListResponse> {
        return traktvCall { service.addTvShowToList(tvShowToTraktList, accessToken) }
    }

    suspend fun getSfListByType(type: String, accessToken: String): Result<GetTraktSfListByType> {
        return traktvCall { service.getStreamFlowListByType(type, accessToken) }
    }

    suspend fun removeMovieFromTraktList(movieFromTraktList: AddMovieToTraktList, accessToken: String): Result<RemoveFromTraktListResponse> {
        return traktvCall { service.removeMovieFromList(movieFromTraktList, accessToken) }
    }

    suspend fun removeTvShowFromTraktList(tvShowToTraktList: AddTvShowToTraktList, accessToken: String): Result<RemoveFromTraktListResponse> {
        return traktvCall { service.removeTvShowFromList(tvShowToTraktList, accessToken) }
    }

    suspend fun createUserFirestore(user: FirebaseUser?): Boolean {
        return try {
            val db = Firebase.firestore

            db.collection("users").document(user!!.uid).set(
                mapOf(
                    "email" to user.email!!
                )
            ).await()
            true
        } catch (e: Exception) {
            false
        }
    }
}