package com.lukakordzaia.imoviesapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WatchedDao {
    @Query("SELECT * FROM watcheddetails")
    fun getWatchedTitles(): LiveData<List<WatchedDetails>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWatchedTitle(watchedDetails: WatchedDetails)

    @Query("SELECT EXISTS (SELECT 1 FROM watcheddetails WHERE titleId = :titleId)")
    fun titleExists(titleId: Int): LiveData<Boolean>

    @Query("UPDATE watcheddetails SET watchedTime = (:watchedTime) WHERE titleId = (:titleId)")
    suspend fun updateTitleInDb(watchedTime: Long, titleId: Int)

    @Query("DELETE FROM watcheddetails WHERE titleId = (:titleId)")
    suspend fun deleteSingleTitle(titleId: Int)

    @Query("DELETE FROM watcheddetails")
    suspend fun deleteDBContent()
}