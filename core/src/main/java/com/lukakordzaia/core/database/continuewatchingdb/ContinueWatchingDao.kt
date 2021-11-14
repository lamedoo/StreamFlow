package com.lukakordzaia.core.database.continuewatchingdb

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ContinueWatchingDao {
    @Query("SELECT * FROM continuewatchingroom")
    fun getContinueWatchingFromRoom(): LiveData<List<ContinueWatchingRoom>>

    @Query("SELECT * FROM continuewatchingroom WHERE titleId = :titleId")
    fun getSingleContinueWatchingFromRoom(titleId: Int) : LiveData<ContinueWatchingRoom>

    @Query("SELECT * FROM continuewatchingroom WHERE titleId = :titleId")
    fun getTvSingleWatchedTitles(titleId: Int) : LiveData<ContinueWatchingRoom>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContinueWatchingInRoom(continueWatchingRoom: ContinueWatchingRoom)

    @Query("UPDATE continuewatchingroom SET watchedDuration = (:watchedTime) WHERE titleId = (:titleId)")
    suspend fun updateTitleInDb(watchedTime: Long, titleId: Int)

    @Query("DELETE FROM continuewatchingroom WHERE titleId = (:titleId)")
    suspend fun deleteSingleContinueWatchingFromRoom(titleId: Int)

    @Query("DELETE FROM continuewatchingroom")
    suspend fun deleteContinueWatchingFullFromRoom()
}