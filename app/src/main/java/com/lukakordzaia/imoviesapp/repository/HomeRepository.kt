package com.lukakordzaia.imoviesapp.repository

import androidx.lifecycle.LiveData
import com.lukakordzaia.imoviesapp.database.WatchedDao
import com.lukakordzaia.imoviesapp.database.WatchedDetails
import com.lukakordzaia.imoviesapp.datamodels.TitleData
import com.lukakordzaia.imoviesapp.datamodels.TitleList
import com.lukakordzaia.imoviesapp.network.Result
import com.lukakordzaia.imoviesapp.network.RetrofitBuilder
import com.lukakordzaia.imoviesapp.network.RetrofitCall

class HomeRepository(private val retrofitBuilder: RetrofitBuilder): RetrofitCall() {
    private val service = retrofitBuilder.buildService()

    suspend fun getNewMovies(page: Int): Result<TitleList> {
        return retrofitCall { service.getNewMovies(page) }
    }

    suspend fun getTopMovies(page: Int): Result<TitleList> {
        return retrofitCall { service.getTopMovies(page) }
    }

    suspend fun getTopTvShows(page: Int): Result<TitleList> {
        return retrofitCall { service.getTopTvShows(page) }
    }

    suspend fun getSingleTitleData(movieId: Int): Result<TitleData> {
        return retrofitCall { service.getSingleTitle(movieId) }
    }

    fun getWatchedFromDb(watchedDao: WatchedDao): LiveData<List<WatchedDetails>> {
        return watchedDao.getWatchedTitles()
    }

    suspend fun deleteSingleTitleFromDb(watchedDao: WatchedDao, titleId: Int) {
        return watchedDao.deleteSingleTitle(titleId)
    }
}