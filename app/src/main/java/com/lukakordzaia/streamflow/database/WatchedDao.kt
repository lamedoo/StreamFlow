package com.lukakordzaia.streamflow.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WatchedDao {
    @Query("SELECT * FROM dbdetails")
    fun getDbTitles(): LiveData<List<DbDetails>>

    @Query("SELECT * FROM dbdetails WHERE titleId = :titleId")
    suspend fun getSingleWatchedTitles(titleId: Int) : DbDetails

    @Query("SELECT * FROM dbdetails WHERE titleId = :titleId")
    fun getTvSingleWatchedTitles(titleId: Int) : LiveData<DbDetails>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWatchedTitle(dbDetails: DbDetails)

    @Query("SELECT EXISTS (SELECT 1 FROM dbdetails WHERE titleId = :titleId)")
    fun titleExists(titleId: Int): LiveData<Boolean>

    @Query("UPDATE dbdetails SET watchedDuration = (:watchedTime) WHERE titleId = (:titleId)")
    suspend fun updateTitleInDb(watchedTime: Long, titleId: Int)

    @Query("DELETE FROM dbdetails WHERE titleId = (:titleId)")
    suspend fun deleteSingleTitle(titleId: Int)

    @Query("DELETE FROM dbdetails")
    suspend fun deleteDBContent()
}