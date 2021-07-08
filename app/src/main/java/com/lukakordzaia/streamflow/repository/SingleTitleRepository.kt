package com.lukakordzaia.streamflow.repository

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.lukakordzaia.streamflow.database.continuewatchingdb.ContinueWatchingRoom
import com.lukakordzaia.streamflow.database.continuewatchingdb.ContinueWatchingDao
import com.lukakordzaia.streamflow.datamodels.*
import com.lukakordzaia.streamflow.network.FirebaseContinueWatchingListCallBack
import com.lukakordzaia.streamflow.network.Result
import com.lukakordzaia.streamflow.network.imovies.ImoviesCall
import com.lukakordzaia.streamflow.network.imovies.ImoviesNetwork
import com.lukakordzaia.streamflow.network.models.response.titles.GetTitlesResponse
import com.lukakordzaia.streamflow.network.models.response.singletitle.GetSingleTitleResponse
import com.lukakordzaia.streamflow.network.models.response.singletitle.GetSingleTitleCastResponse
import com.lukakordzaia.streamflow.network.models.response.singletitle.GetSingleTitleFilesResponse
import kotlinx.coroutines.tasks.await

class SingleTitleRepository(private val service: ImoviesNetwork): ImoviesCall() {
    suspend fun getSingleTitleData(titleId: Int): Result<GetSingleTitleResponse> {
        return imoviesCall { service.getSingleTitle(titleId) }
    }

    fun checkContinueWatchingTitleInRoom(continueWatchingDao: ContinueWatchingDao, titleId: Int): LiveData<Boolean> {
        return continueWatchingDao.checkContinueWatchingTitleInRoom(titleId)
    }

    suspend fun getSingleContinueWatchingFromRoom(continueWatchingDao: ContinueWatchingDao, titleId: Int): ContinueWatchingRoom {
        return continueWatchingDao.getSingleContinueWatchingFromRoom(titleId)
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

    suspend fun checkContinueWatchingInFirestore(currentUserUid: String, titleId: Int): DocumentSnapshot? {
        return try {
            val data = Firebase.firestore
                    .collection("users")
                    .document(currentUserUid)
                    .collection("continueWatching")
                    .document(titleId.toString())
                    .get()
                    .await()

            data
        } catch (e: Exception) {
            null
        }
    }

    fun checkContinueWatchingInFirestore1(currentUserUid: String, titleId: Int, continueWatchingListCallBack: FirebaseContinueWatchingListCallBack) {
        val docRef = Firebase.firestore.collection("users").document(currentUserUid).collection("continueWatching").document(titleId.toString())
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(ContentValues.TAG, "Listen failed.", e)
                return@addSnapshotListener
            }
            if (snapshot?.data != null ) {
                Log.d(ContentValues.TAG, snapshot.data.toString())
                val titleList: MutableList<ContinueWatchingRoom> = ArrayList()
                    titleList.add( ContinueWatchingRoom(
                            snapshot.data?.get("id").toString().toInt(),
                            snapshot.data?.get("language").toString(),
                            snapshot.data?.get("continueFrom") as Long,
                            snapshot.data?.get("titleDuration") as Long,
                            snapshot.data?.get("isTvShow") as Boolean,
                            snapshot.data?.get("season").toString().toInt(),
                            snapshot.data?.get("episode").toString().toInt()
                    )
                    )
                continueWatchingListCallBack.continueWatchingList(titleList)
            } else {
                Log.d(ContentValues.TAG, "Current data: null")
            }
        }
    }

    suspend fun addWatchedEpisodeToFirestore(currentUserUid: String, continueWatchingRoom: ContinueWatchingRoom): Boolean {
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