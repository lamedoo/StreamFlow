package com.lukakordzaia.medootv.repository

import androidx.lifecycle.LiveData
import com.lukakordzaia.medootv.database.DbDetails
import com.lukakordzaia.medootv.database.WatchedDao
import com.lukakordzaia.medootv.datamodels.TitleData
import com.lukakordzaia.medootv.datamodels.TitleList
import com.lukakordzaia.medootv.network.Result
import com.lukakordzaia.medootv.network.RetrofitBuilder
import com.lukakordzaia.medootv.network.RetrofitCall

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

    fun getDbTitles(watchedDao: WatchedDao): LiveData<List<DbDetails>> {
        return watchedDao.getDbTitles()
    }

    suspend fun deleteSingleTitleFromDb(watchedDao: WatchedDao, titleId: Int) {
        return watchedDao.deleteSingleTitle(titleId)
    }
}