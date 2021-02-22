package com.lukakordzaia.streamflow.repository

import androidx.lifecycle.LiveData
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.lukakordzaia.streamflow.database.DbDetails
import com.lukakordzaia.streamflow.database.WatchedDao
import com.lukakordzaia.streamflow.datamodels.SingleTitleData
import com.lukakordzaia.streamflow.datamodels.TitleList
import com.lukakordzaia.streamflow.network.Result
import com.lukakordzaia.streamflow.network.RetrofitBuilder
import com.lukakordzaia.streamflow.network.imovies.ImoviesCall
import kotlinx.coroutines.tasks.await

class HomeRepository(private val retrofitBuilder: RetrofitBuilder): ImoviesCall() {
    private val service = retrofitBuilder.buildImoviesService()

    suspend fun getMovieDay(): Result<TitleList> {
        return imoviesCall { service.getMovieDay() }
    }

    suspend fun getNewMovies(page: Int): Result<TitleList> {
        return imoviesCall { service.getNewMovies(page) }
    }

    suspend fun getTopMovies(page: Int): Result<TitleList> {
        return imoviesCall { service.getTopMovies(page) }
    }

    suspend fun getTopTvShows(page: Int): Result<TitleList> {
        return imoviesCall { service.getTopTvShows(page) }
    }

    suspend fun getSingleTitleData(movieId: Int): Result<SingleTitleData> {
        return imoviesCall { service.getSingleTitle(movieId) }
    }

    fun getDbTitles(watchedDao: WatchedDao): LiveData<List<DbDetails>> {
        return watchedDao.getDbTitles()
    }

    suspend fun deleteSingleTitleFromDb(watchedDao: WatchedDao, titleId: Int) {
        return watchedDao.deleteSingleTitle(titleId)
    }

    suspend fun getContinueWatchingFromFirestore(currentUserUid: String) : QuerySnapshot? {
        return try {
            val data = Firebase.firestore
                    .collection("users")
                    .document(currentUserUid)
                    .collection("continueWatching")
                    .get()
                    .await()
            data
        } catch (e: Exception) {
            null
        }
    }
}