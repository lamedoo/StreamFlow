package com.lukakordzaia.streamflow.repository

import androidx.lifecycle.LiveData
import com.lukakordzaia.streamflow.database.DbDetails
import com.lukakordzaia.streamflow.database.WatchedDao
import com.lukakordzaia.streamflow.datamodels.TitleCast
import com.lukakordzaia.streamflow.datamodels.SingleTitleData
import com.lukakordzaia.streamflow.datamodels.TitleFiles
import com.lukakordzaia.streamflow.datamodels.TitleList
import com.lukakordzaia.streamflow.network.Result
import com.lukakordzaia.streamflow.network.RetrofitBuilder
import com.lukakordzaia.streamflow.network.imovies.ImoviesCall

class TvDetailsRepository(private val retrofitBuilder: RetrofitBuilder): ImoviesCall() {
    private val service = retrofitBuilder.buildImoviesService()

    suspend fun getSingleTitleData(titleId: Int): Result<SingleTitleData> {
        return imoviesCall { service.getSingleTitle(titleId) }
    }

    fun checkTitleInDb(watchedDao: WatchedDao, titleId: Int): LiveData<Boolean> {
        return watchedDao.titleExists(titleId)
    }

    fun getSingleWatchedTitles(watchedDao: WatchedDao, titleId: Int): LiveData<DbDetails> {
        return watchedDao.getTvSingleWatchedTitles(titleId)
    }

    suspend fun getSingleTitleFiles(titleId: Int, season_number: Int = 1): Result<TitleFiles> {
        return imoviesCall { service.getSingleFiles(titleId, season_number) }
    }

    suspend fun getSingleTitleCast(titleId: Int, role: String) : Result<TitleCast> {
        return imoviesCall { service.getSingleTitleCast(titleId, role) }
    }

    suspend fun getSingleTitleRelated(titleId: Int): Result<TitleList> {
        return imoviesCall { service.getSingleTitleRelated(titleId) }
    }

    suspend fun deleteTitleFromDb(watchedDao: WatchedDao, titleId: Int) {
        watchedDao.deleteSingleTitle(titleId)
    }
}