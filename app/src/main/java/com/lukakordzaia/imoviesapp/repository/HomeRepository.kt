package com.lukakordzaia.imoviesapp.repository

import androidx.lifecycle.LiveData
import com.lukakordzaia.imoviesapp.database.WatchedDao
import com.lukakordzaia.imoviesapp.database.WatchedDetails
import com.lukakordzaia.imoviesapp.network.Result
import com.lukakordzaia.imoviesapp.network.RetrofitBuilder
import com.lukakordzaia.imoviesapp.network.TitlesNetwork
import com.lukakordzaia.imoviesapp.network.datamodels.TitleData
import com.lukakordzaia.imoviesapp.network.datamodels.TitleList

class HomeRepository {
    private val destinationService = RetrofitBuilder.buildService(TitlesNetwork::class.java)

    suspend fun getTopMovies(): Result<TitleList> {
        return RetrofitBuilder.retrofitCall { destinationService.getTopMovies() }
    }

    suspend fun getTopTvShows(): Result<TitleList> {
        return RetrofitBuilder.retrofitCall { destinationService.getTopTvShows() }
    }

    suspend fun getSingleTitleData(movieId: Int): Result<TitleData> {
        return RetrofitBuilder.retrofitCall { destinationService.getSingleTitle(movieId) }
    }

    fun getWatchedFromDb(watchedDao: WatchedDao): LiveData<List<WatchedDetails>> {
        return watchedDao.getWatchedTitles()
    }
}