package com.lukakordzaia.core.domain.repository.databaserepository

import androidx.lifecycle.LiveData
import com.lukakordzaia.core.database.continuewatchingdb.ContinueWatchingRoom

interface DatabaseRepository {
    fun getContinueWatchingFromRoom(): LiveData<List<ContinueWatchingRoom>>
    suspend fun deleteSingleContinueWatchingFromRoom(titleId: Int): Long
    fun getSingleContinueWatchingFromRoom(titleId: Int): LiveData<ContinueWatchingRoom>
    suspend fun insertContinueWatchingInRoom(continueWatchingRoom: ContinueWatchingRoom)
    suspend fun deleteAllFromRoom()
}