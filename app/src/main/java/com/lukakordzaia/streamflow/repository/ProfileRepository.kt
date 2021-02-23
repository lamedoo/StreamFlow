package com.lukakordzaia.streamflow.repository

import androidx.lifecycle.LiveData
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.lukakordzaia.streamflow.database.DbDetails
import com.lukakordzaia.streamflow.database.WatchedDao
import kotlinx.coroutines.tasks.await

class ProfileRepository {
    fun getContinueWatchingFromRoom(watchedDao: WatchedDao): LiveData<List<DbDetails>> {
        return watchedDao.getContinueWatchingFromRoom()
    }

    suspend fun addContinueWatchingTitleToFirestore(currentUserUid: String, dbDetails: DbDetails): Boolean {
        return try {
            Firebase.firestore
                    .collection("users")
                    .document(currentUserUid)
                    .collection("continueWatching")
                    .document(dbDetails.titleId.toString())
                    .set(
                            mapOf(
                                    "id" to dbDetails.titleId,
                                    "language" to dbDetails.language,
                                    "isTvShow" to dbDetails.isTvShow,
                                    "continueFrom" to dbDetails.watchedDuration,
                                    "titleDuration" to dbDetails.titleDuration,
                                    "season" to dbDetails.season,
                                    "episode" to dbDetails.episode
                            )
                    )
                    .await()
            true
        } catch (e: Exception) {
            false
        }
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

    suspend fun deleteContinueWatchingFullFromFirestore(currentUserUid: String, titleId: Int): Boolean {
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