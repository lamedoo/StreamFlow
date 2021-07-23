package com.lukakordzaia.streamflow.repository

import androidx.lifecycle.LiveData
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.lukakordzaia.streamflow.database.continuewatchingdb.ContinueWatchingRoom
import com.lukakordzaia.streamflow.database.continuewatchingdb.ContinueWatchingDao
import kotlinx.coroutines.tasks.await

class ProfileRepository {
    fun getContinueWatchingFromRoom(continueWatchingDao: ContinueWatchingDao): LiveData<List<ContinueWatchingRoom>> {
        return continueWatchingDao.getContinueWatchingFromRoom()
    }

    suspend fun addContinueWatchingTitleToFirestore(currentUserUid: String, continueWatchingRoom: ContinueWatchingRoom): Boolean {
        return try {
            Firebase.firestore
                    .collection("users")
                    .document(currentUserUid)
                    .collection("continueWatching")
                    .document(continueWatchingRoom.titleId.toString())
                    .set(
                            mapOf(
                                    "id" to continueWatchingRoom.titleId,
                                    "language" to continueWatchingRoom.language,
                                    "isTvShow" to continueWatchingRoom.isTvShow,
                                    "continueFrom" to continueWatchingRoom.watchedDuration,
                                    "titleDuration" to continueWatchingRoom.titleDuration,
                                    "season" to continueWatchingRoom.season,
                                    "episode" to continueWatchingRoom.episode
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