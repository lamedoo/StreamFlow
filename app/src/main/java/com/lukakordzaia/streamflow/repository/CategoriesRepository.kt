package com.lukakordzaia.streamflow.repository

import com.lukakordzaia.streamflow.datamodels.GenreList
import com.lukakordzaia.streamflow.datamodels.StudioList
import com.lukakordzaia.streamflow.datamodels.TitleList
import com.lukakordzaia.streamflow.network.Result
import com.lukakordzaia.streamflow.network.RetrofitBuilder
import com.lukakordzaia.streamflow.network.RetrofitCall

class CategoriesRepository(private val retrofitBuilder: RetrofitBuilder): RetrofitCall() {
    private val service = retrofitBuilder.buildService()

    suspend fun getAllGenres(): Result<GenreList> {
        return retrofitCall { service.getAllGenres() }
    }

    suspend fun getSingleGenre(genreId: Int, page: Int): Result<TitleList> {
        return retrofitCall { service.getSingleGenre(genreId, page) }
    }

    suspend fun getTopStudios(): Result<StudioList> {
        return retrofitCall { service.getTopStudios() }
    }

    suspend fun getSingleStudio(studioId: Int, page: Int): Result<TitleList> {
        return retrofitCall { service.getSingleStudio(studioId, page) }
    }
}