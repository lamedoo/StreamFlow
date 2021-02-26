package com.lukakordzaia.streamflow.repository

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.lukakordzaia.streamflow.database.DbDetails
import com.lukakordzaia.streamflow.database.WatchedDao
import com.lukakordzaia.streamflow.datamodels.SingleTitleData
import com.lukakordzaia.streamflow.datamodels.TitleList
import com.lukakordzaia.streamflow.network.*
import com.lukakordzaia.streamflow.network.imovies.ImoviesCall
import kotlinx.coroutines.tasks.await

class HomeRepository(retrofitBuilder: RetrofitBuilder): ImoviesCall() {
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

    fun getContinueWatchingFromRoom(watchedDao: WatchedDao): LiveData<List<DbDetails>> {
        return watchedDao.getContinueWatchingFromRoom()
    }

    suspend fun deleteSingleContinueWatchingFromRoom(watchedDao: WatchedDao, titleId: Int) {
        return watchedDao.deleteSingleContinueWatchingFromRoom(titleId)
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

    fun getContinueWatchingFromFirestore1(currentUserUid: String, continueWatchingListCallBack: FirebaseContinueWatchingListCallBack) {
        val docRef = Firebase.firestore.collection("users").document(currentUserUid).collection("continueWatching")
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(ContentValues.TAG, "Listen failed.", e)
                return@addSnapshotListener
            }
            if (snapshot != null ) {
                val titleList: MutableList<DbDetails> = ArrayList()
                for (title in snapshot) {
                    titleList.add( DbDetails(
                            title.data["id"].toString().toInt(),
                            title.data["language"].toString(),
                            title.data["continueFrom"] as Long,
                            title.data["titleDuration"] as Long,
                            title.data["isTvShow"] as Boolean,
                            title.data["season"].toString().toInt(),
                            title.data["episode"].toString().toInt()
                    ))
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