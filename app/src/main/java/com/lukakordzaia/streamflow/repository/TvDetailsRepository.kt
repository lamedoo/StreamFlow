package com.lukakordzaia.streamflow.repository

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.lukakordzaia.streamflow.database.DbDetails
import com.lukakordzaia.streamflow.database.WatchedDao
import com.lukakordzaia.streamflow.datamodels.*
import com.lukakordzaia.streamflow.network.*
import com.lukakordzaia.streamflow.network.imovies.ImoviesCall
import kotlinx.coroutines.tasks.await

class TvDetailsRepository(retrofitBuilder: RetrofitBuilder) : ImoviesCall() {
    private val service = retrofitBuilder.buildImoviesService()

    suspend fun getSingleTitleData(titleId: Int): Result<SingleTitleData> {
        return imoviesCall { service.getSingleTitle(titleId) }
    }

    fun checkContinueWatchingTitleInRoom(watchedDao: WatchedDao, titleId: Int): LiveData<Boolean> {
        return watchedDao.checkContinueWatchingTitleInRoom(titleId)
    }

    suspend fun getSingleContinueWatchingFromRoom(watchedDao: WatchedDao, titleId: Int): DbDetails {
        return watchedDao.getSingleContinueWatchingFromRoom(titleId)
    }

    suspend fun getSingleTitleFiles(titleId: Int, season_number: Int = 1): Result<TitleFiles> {
        return imoviesCall { service.getSingleFiles(titleId, season_number) }
    }

    suspend fun getSingleTitleCast(titleId: Int, role: String): Result<TitleCast> {
        return imoviesCall { service.getSingleTitleCast(titleId, role) }
    }

    suspend fun getSingleTitleRelated(titleId: Int): Result<TitleList> {
        return imoviesCall { service.getSingleTitleRelated(titleId) }
    }

    suspend fun deleteSingleContinueWatchingFromRoom(watchedDao: WatchedDao, titleId: Int) {
        watchedDao.deleteSingleContinueWatchingFromRoom(titleId)
    }

    suspend fun addFavTitleToFirestore(currentUserUid: String, addTitleToFirestore: AddTitleToFirestore): Boolean {
        return try {
            Firebase.firestore
                    .collection("users")
                    .document(currentUserUid)
                    .collection("favMovies")
                    .document(addTitleToFirestore.id.toString())
                    .set(
                            mapOf(
                                    "name" to addTitleToFirestore.name,
                                    "isTvShow" to addTitleToFirestore.isTvShow,
                                    "id" to addTitleToFirestore.id,
                                    "imdbId" to addTitleToFirestore.imdbId
                            )
                    )
                    .await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun removeFavTitleFromFirestore(currentUserUid: String, titleId: Int): Boolean {
        return try {
            Firebase.firestore
                    .collection("users")
                    .document(currentUserUid)
                    .collection("favMovies")
                    .document(titleId.toString())
                    .delete()
                    .await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun checkTitleInFirestore(currentUserUid: String, titleId: Int): DocumentSnapshot? {
        return try {
            val data = Firebase.firestore
                    .collection("users")
                    .document(currentUserUid)
                    .collection("favMovies")
                    .document(titleId.toString())
                    .get()
                    .await()

            data
        } catch (e: Exception) {
            null
        }
    }

    fun checkContinueWatchingInFirestore(currentUserUid: String, titleId: Int, continueWatchingCallBack: FirebaseContinueWatchingCallBack) {
        val docRef = Firebase.firestore.collection("users").document(currentUserUid).collection("continueWatching").document(titleId.toString())
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(ContentValues.TAG, "Listen failed.", e)
                return@addSnapshotListener
            }
            if (snapshot!!.data != null) {
                val title = DbDetails(
                        snapshot.data!!["id"].toString().toInt(),
                        snapshot.data!!["language"].toString(),
                        snapshot.data!!["continueFrom"] as Long,
                        snapshot.data!!["titleDuration"] as Long,
                        snapshot.data!!["isTvShow"] as Boolean,
                        snapshot.data!!["season"].toString().toInt(),
                        snapshot.data!!["episode"].toString().toInt()
                )
                continueWatchingCallBack.continueWatchingTitle(title)
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