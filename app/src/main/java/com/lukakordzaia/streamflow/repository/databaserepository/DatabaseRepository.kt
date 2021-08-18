package com.lukakordzaia.streamflow.repository.databaserepository

import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseUser
import com.lukakordzaia.streamflow.database.continuewatchingdb.ContinueWatchingRoom
import com.lukakordzaia.streamflow.network.FirebaseContinueWatchingCallBack
import com.lukakordzaia.streamflow.network.FirebaseContinueWatchingListCallBack

interface DatabaseRepository {
    fun getContinueWatchingFromRoom(): LiveData<List<ContinueWatchingRoom>>
    suspend fun deleteSingleContinueWatchingFromRoom(titleId: Int)
    fun getSingleContinueWatchingFromRoom(titleId: Int): LiveData<ContinueWatchingRoom>
    suspend fun insertContinueWatchingInRoom(continueWatchingRoom: ContinueWatchingRoom)
    suspend fun deleteAllFromRoom()

    suspend fun createUserFirestore(user: FirebaseUser?): Boolean

    fun getContinueWatchingFromFirestore(currentUserUid: String, continueWatchingListCallBack: FirebaseContinueWatchingListCallBack)
    suspend fun deleteSingleContinueWatchingFromFirestore(currentUserUid: String, titleId: Int): Boolean
    suspend fun addContinueWatchingTitleToFirestore(currentUserUid: String, continueWatchingRoom: ContinueWatchingRoom): Boolean
    fun checkContinueWatchingInFirestore(currentUserUid: String, titleId: Int, continueWatchingCallBack: FirebaseContinueWatchingCallBack)
    suspend fun addWatchedEpisodeToFirestore(currentUserUid: String, continueWatchingRoom: ContinueWatchingRoom): Boolean
}