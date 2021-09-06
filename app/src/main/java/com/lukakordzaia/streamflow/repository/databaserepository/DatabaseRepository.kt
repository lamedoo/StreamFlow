package com.lukakordzaia.streamflow.repository.databaserepository

import androidx.lifecycle.LiveData
import com.lukakordzaia.streamflow.database.continuewatchingdb.ContinueWatchingRoom

interface DatabaseRepository {
    fun getContinueWatchingFromRoom(): LiveData<List<ContinueWatchingRoom>>
    suspend fun deleteSingleContinueWatchingFromRoom(titleId: Int)
    fun getSingleContinueWatchingFromRoom(titleId: Int): LiveData<ContinueWatchingRoom>
    suspend fun insertContinueWatchingInRoom(continueWatchingRoom: ContinueWatchingRoom)
    suspend fun deleteAllFromRoom()
}