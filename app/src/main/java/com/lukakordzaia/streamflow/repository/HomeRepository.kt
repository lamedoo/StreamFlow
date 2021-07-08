package com.lukakordzaia.streamflow.repository

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.lukakordzaia.streamflow.database.continuewatchingdb.ContinueWatchingRoom
import com.lukakordzaia.streamflow.database.continuewatchingdb.ContinueWatchingDao
import com.lukakordzaia.streamflow.network.models.response.singletitle.GetSingleTitleResponse
import com.lukakordzaia.streamflow.network.models.response.titles.GetTitlesResponse
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

    suspend fun getSingleTitleData(movieId: Int): Result<GetSingleTitleResponse> {
        return imoviesCall { service.getSingleTitle(movieId) }
    }

    fun getContinueWatchingFromRoom(continueWatchingDao: ContinueWatchingDao): LiveData<List<ContinueWatchingRoom>> {
        return continueWatchingDao.getContinueWatchingFromRoom()
    }

    suspend fun deleteSingleContinueWatchingFromRoom(continueWatchingDao: ContinueWatchingDao, titleId: Int) {
        return continueWatchingDao.deleteSingleContinueWatchingFromRoom(titleId)
    }

    fun getContinueWatchingFromFirestore(currentUserUid: String, continueWatchingListCallBack: FirebaseContinueWatchingListCallBack) {
        val docRef = Firebase.firestore.collection("users").document(currentUserUid).collection("continueWatching")
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(ContentValues.TAG, "Listen failed.", e)
                return@addSnapshotListener
            }
            if (snapshot != null ) {
                val titleList: MutableList<ContinueWatchingRoom> = ArrayList()
                for (title in snapshot) {
                    titleList.add( ContinueWatchingRoom(
                            title.data["id"].toString().toInt(),
                            title.data["language"].toString(),
                            title.data["continueFrom"] as Long,
                            title.data["titleDuration"] as Long,
                            title.data["isTvShow"] as Boolean,
                            title.data["season"].toString().toInt(),
                            title.data["episode"].toString().toInt()
                    )
                    )
                }
                continueWatchingListCallBack.continueWatchingList(titleList)
            } else {
                Log.d(ContentValues.TAG, "Current data: null")
            }
        }
    }

    suspend fun deleteSingleContinueWatchingFromFirestore(currentUserUid: String, titleId: Int): Boolean {
        return try {
            Firebase.firestore
                    .collection("users")
                    .document(currentUserUid)
                    .collection("continueWatching")
                    .document(titleId.toString())
                    .delete()
                    .await()
            true
        } catch (e: Exception) {
            false
        }
    }
}