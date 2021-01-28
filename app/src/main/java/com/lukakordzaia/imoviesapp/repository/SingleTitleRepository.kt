package com.lukakordzaia.imoviesapp.repository

import androidx.lifecycle.LiveData
import com.lukakordzaia.imoviesapp.database.WatchedDao
import com.lukakordzaia.imoviesapp.database.WatchedDetails
import com.lukakordzaia.imoviesapp.datamodels.TitleData
import com.lukakordzaia.imoviesapp.datamodels.TitleFiles
import com.lukakordzaia.imoviesapp.network.Result
import com.lukakordzaia.imoviesapp.network.RetrofitBuilder
import com.lukakordzaia.imoviesapp.network.TitlesNetwork

class SingleTitleRepository {
    private val destinationService = RetrofitBuilder.buildService(TitlesNetwork::class.java)

    suspend fun getSingleTitleData(titleId: Int): Result<TitleData> {
        return RetrofitBuilder.retrofitCall { destinationService.getSingleTitle(titleId) }
    }

    fun checkTitleInDb(watchedDao: WatchedDao, titleId: Int): LiveData<Boolean> {
        return watchedDao.titleExists(titleId)
    }

    fun getSingleWatchedTitles(watchedDao: WatchedDao, titleId: Int): LiveData<WatchedDetails> {
        return watchedDao.getSingleWatchedTitles(titleId)
    }

    suspend fun getSingleTitleFiles(titleId: Int, season_number: Int = 1): Result<TitleFiles> {
        return RetrofitBuilder.retrofitCall { destinationService.getSingleFiles(titleId, season_number) }
    }

    suspend fun deleteTitleFromDb(watchedDao: WatchedDao, titleId: Int) {
        watchedDao.deleteSingleTitle(titleId)
    }
}