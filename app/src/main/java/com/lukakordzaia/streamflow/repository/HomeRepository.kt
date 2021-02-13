package com.lukakordzaia.streamflow.repository

import androidx.lifecycle.LiveData
import com.lukakordzaia.streamflow.database.DbDetails
import com.lukakordzaia.streamflow.database.WatchedDao
import com.lukakordzaia.streamflow.datamodels.SingleTitleData
import com.lukakordzaia.streamflow.datamodels.TitleList
import com.lukakordzaia.streamflow.network.Result
import com.lukakordzaia.streamflow.network.RetrofitBuilder
import com.lukakordzaia.streamflow.network.RetrofitCall

class HomeRepository(private val retrofitBuilder: RetrofitBuilder): RetrofitCall() {
    private val service = retrofitBuilder.buildService()

    suspend fun getMovieDay(): Result<TitleList> {
        return retrofitCall { service.getMovieDay() }
    }

    suspend fun getNewMovies(page: Int): Result<TitleList> {
        return retrofitCall { service.getNewMovies(page) }
    }

    suspend fun getTopMovies(page: Int): Result<TitleList> {
        return retrofitCall { service.getTopMovies(page) }
    }

    suspend fun getTopTvShows(page: Int): Result<TitleList> {
        return retrofitCall { service.getTopTvShows(page) }
    }

    suspend fun getSingleTitleData(movieId: Int): Result<SingleTitleData> {
        return retrofitCall { service.getSingleTitle(movieId) }
    }

    fun getDbTitles(watchedDao: WatchedDao): LiveData<List<DbDetails>> {
        return watchedDao.getDbTitles()
    }

    suspend fun deleteSingleTitleFromDb(watchedDao: WatchedDao, titleId: Int) {
        return watchedDao.deleteSingleTitle(titleId)
    }
}