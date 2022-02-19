package com.lukakordzaia.core.domain.repository.databaserepository

import androidx.lifecycle.LiveData
import com.lukakordzaia.core.database.StreamFlowDatabase
import com.lukakordzaia.core.database.continuewatchingdb.ContinueWatchingRoom

class DefaultDatabaseRepository(private val database: StreamFlowDatabase): DatabaseRepository {
    override fun getContinueWatchingFromRoom(): LiveData<List<ContinueWatchingRoom>> {
        return database.continueWatchingDao().getContinueWatchingFromRoom()
    }

    override suspend fun deleteSingleContinueWatchingFromRoom(titleId: Int): Int {
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
}