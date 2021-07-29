package com.lukakordzaia.streamflow.repository

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.lukakordzaia.streamflow.database.StreamFlowDatabase
import com.lukakordzaia.streamflow.database.continuewatchingdb.ContinueWatchingRoom
import com.lukakordzaia.streamflow.database.continuewatchingdb.ContinueWatchingDao
import com.lukakordzaia.streamflow.network.models.imovies.response.singletitle.GetSingleTitleResponse
import com.lukakordzaia.streamflow.network.models.imovies.response.titles.GetTitlesResponse
import com.lukakordzaia.streamflow.network.*
import com.lukakordzaia.streamflow.network.imovies.ImoviesCall
import com.lukakordzaia.streamflow.network.imovies.ImoviesNetwork
import kotlinx.coroutines.tasks.await

class HomeRepository(private val service: ImoviesNetwork): ImoviesCall() {
    suspend fun getMovieDay(): Result<GetTitlesResponse> {
        return imoviesCall { service.getMovieDay() }
    }

    suspend fun getNewMovies(page: Int): Result<GetTitlesResponse> {
        return imoviesCall { service.getNewMovies(page) }
    }

    suspend fun getTopMovies(page: Int): Result<GetTitlesResponse> {
        return imoviesCall { service.getTopMovies(page) }
    }

    suspend fun getTopTvShows(page: Int): Result<GetTitlesResponse> {
        return imoviesCall { service.getTopTvShows(page) }
    }
}