package com.lukakordzaia.streamflow.repository

import androidx.lifecycle.LiveData
import com.lukakordzaia.streamflow.database.DbDetails
import com.lukakordzaia.streamflow.database.WatchedDao
import com.lukakordzaia.streamflow.datamodels.TitleCast
import com.lukakordzaia.streamflow.datamodels.TitleData
import com.lukakordzaia.streamflow.datamodels.TitleFiles
import com.lukakordzaia.streamflow.datamodels.TitleList
import com.lukakordzaia.streamflow.network.Result
import com.lukakordzaia.streamflow.network.RetrofitBuilder
import com.lukakordzaia.streamflow.network.RetrofitCall

class SingleTitleRepository(private val retrofitBuilder: RetrofitBuilder): RetrofitCall() {
    private val service = retrofitBuilder.buildService()

    suspend fun getSingleTitleData(titleId: Int): Result<TitleData> {
        return retrofitCall { service.getSingleTitle(titleId) }
    }

    fun checkTitleInDb(watchedDao: WatchedDao, titleId: Int): LiveData<Boolean> {
        return watchedDao.titleExists(titleId)
    }

    fun getSingleWatchedTitles(watchedDao: WatchedDao, titleId: Int): LiveData<DbDetails> {
        return watchedDao.getSingleWatchedTitles(titleId)
    }

    suspend fun getSingleTitleFiles(titleId: Int, season_number: Int = 1): Result<TitleFiles> {
        return retrofitCall { service.getSingleFiles(titleId, season_number) }
    }

    suspend fun getSingleTitleCast(titleId: Int, role: String) : Result<TitleCast> {
        return retrofitCall { service.getSingleTitleCast(titleId, role) }
    }

    suspend fun getSingleTitleRelated(titleId: Int): Result<TitleList> {
        return retrofitCall { service.getSingleTitleRelated(titleId) }
    }

    suspend fun deleteTitleFromDb(watchedDao: WatchedDao, titleId: Int) {
        watchedDao.deleteSingleTitle(titleId)
    }
}