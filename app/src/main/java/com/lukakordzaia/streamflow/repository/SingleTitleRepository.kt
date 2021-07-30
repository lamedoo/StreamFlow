package com.lukakordzaia.streamflow.repository

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.lukakordzaia.streamflow.database.StreamFlowDatabase
import com.lukakordzaia.streamflow.database.continuewatchingdb.ContinueWatchingRoom
import com.lukakordzaia.streamflow.database.continuewatchingdb.ContinueWatchingDao
import com.lukakordzaia.streamflow.datamodels.*
import com.lukakordzaia.streamflow.network.FirebaseContinueWatchingListCallBack
import com.lukakordzaia.streamflow.network.Result
import com.lukakordzaia.streamflow.network.imovies.ImoviesCall
import com.lukakordzaia.streamflow.network.imovies.ImoviesNetwork
import com.lukakordzaia.streamflow.network.models.imovies.response.titles.GetTitlesResponse
import com.lukakordzaia.streamflow.network.models.imovies.response.singletitle.GetSingleTitleResponse
import com.lukakordzaia.streamflow.network.models.imovies.response.singletitle.GetSingleTitleCastResponse
import com.lukakordzaia.streamflow.network.models.imovies.response.singletitle.GetSingleTitleFilesResponse
import kotlinx.coroutines.tasks.await

class SingleTitleRepository(private val service: ImoviesNetwork): ImoviesCall() {
    suspend fun getSingleTitleData(titleId: Int): Result<GetSingleTitleResponse> {
        return imoviesCall { service.getSingleTitle(titleId) }
    }

    suspend fun getSingleTitleFiles(titleId: Int, season_number: Int = 1): Result<GetSingleTitleFilesResponse> {
        return imoviesCall { service.getSingleTitleFiles(titleId, season_number) }
    }

    suspend fun getSingleTitleCast(titleId: Int, role: String): Result<GetSingleTitleCastResponse> {
        return imoviesCall { service.getSingleTitleCast(titleId, role) }
    }

    suspend fun getSingleTitleRelated(titleId: Int): Result<GetTitlesResponse> {
        return imoviesCall { service.getSingleTitleRelated(titleId) }
    }
}