package com.lukakordzaia.streamflow.repository.databaserepository

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.lukakordzaia.streamflow.database.StreamFlowDatabase
import com.lukakordzaia.streamflow.database.continuewatchingdb.ContinueWatchingRoom
import com.lukakordzaia.streamflow.network.FirebaseContinueWatchingCallBack
import com.lukakordzaia.streamflow.network.FirebaseContinueWatchingListCallBack
import kotlinx.coroutines.tasks.await

class DefaultDatabaseRepository(private val database: StreamFlowDatabase): DatabaseRepository {
    override fun getContinueWatchingFromRoom(): LiveData<List<ContinueWatchingRoom>> {
        return database.continueWatchingDao().getContinueWatchingFromRoom()
    }

    override suspend fun deleteSingleContinueWatchingFromRoom(titleId: Int) {
        return database.continueWatchingDao().deleteSingleContinueWatchingFromRoom(titleId)
    }

    override fun getSingleContinueWatchingFromRoom(titleId: Int): LiveData<ContinueWatchingRoom> {
        return database.continueWatchingDao().getSingleContinueWatchingFromRoom(titleId)
    }

    override suspend fun insertContinueWatchingInRoom(continueWatchingRoom: ContinueWatchingRoom) {
        database.continueWatchingDao().insertContinueWatchingInRoom(continueWatchingRoom)
    }

    override suspend fun deleteAllFromRoom() {
        database.continueWatchingDao().deleteContinueWatchingFullFromRoom()
    }

    override suspend fun createUserFirestore(user: FirebaseUser?): Boolean {
        return try {
            val db = Firebase.firestore

            db.collection("users").document(user!!.uid).set(
                mapOf(
                    "email" to user.email!!
                )
            ).await()
            true
        } catch (e: java.lang.Exception) {
            false
        }
    }

    override fun getContinueWatchingFromFirestore(currentUserUid: String, continueWatchingListCallBack: FirebaseContinueWatchingListCallBack) {
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

    override suspend fun deleteSingleContinueWatchingFromFirestore(currentUserUid: String, titleId: Int): Boolean {
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

    override suspend fun addContinueWatchingTitleToFirestore(currentUserUid: String, continueWatchingRoom: ContinueWatchingRoom): Boolean {
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

    override fun checkContinueWatchingInFirestore(currentUserUid: String, titleId: Int, continueWatchingCallBack: FirebaseContinueWatchingCallBack) {
        val docRef = Firebase.firestore.collection("users").document(currentUserUid).collection("continueWatching").document(titleId.toString())
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(ContentValues.TAG, "Listen failed.", e)
                return@addSnapshotListener
            }
            if (snapshot!!.data != null) {
                val title = ContinueWatchingRoom(
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
                continueWatchingCallBack.continueWatchingTitle(null)
                Log.d(ContentValues.TAG, "Current data: null")
            }
        }
    }

    override suspend fun addWatchedEpisodeToFirestore(currentUserUid: String, continueWatchingRoom: ContinueWatchingRoom): Boolean {
        return try {
            Firebase.firestore
                .collection("users")
                .document(currentUserUid)
                .collection("watchedEpisodes")
                .document(continueWatchingRoom.titleId.toString())
                .collection("season ${continueWatchingRoom.season}")
                .document("episode ${continueWatchingRoom.episode}")
                .set(
                    mapOf(
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
}