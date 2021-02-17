package com.lukakordzaia.streamflow.repository

import com.lukakordzaia.streamflow.datamodels.GenreList
import com.lukakordzaia.streamflow.datamodels.StudioList
import com.lukakordzaia.streamflow.datamodels.TitleList
import com.lukakordzaia.streamflow.network.Result
import com.lukakordzaia.streamflow.network.RetrofitBuilder
import com.lukakordzaia.streamflow.network.imovies.ImoviesCall

class CategoriesRepository(retrofitBuilder: RetrofitBuilder): ImoviesCall() {
    private val service = retrofitBuilder.buildImoviesService()

    suspend fun getAllGenres(): Result<GenreList> {
        return imoviesCall { service.getAllGenres() }
    }

    suspend fun getSingleGenre(genreId: Int, page: Int): Result<TitleList> {
        return imoviesCall { service.getSingleGenre(genreId, page) }
    }

    suspend fun getTopStudios(): Result<StudioList> {
        return imoviesCall { service.getTopStudios() }
    }

    suspend fun getSingleStudio(studioId: Int, page: Int): Result<TitleList> {
        return imoviesCall { service.getSingleStudio(studioId, page) }
    }

    suspend fun getTopTrailers(): Result<TitleList> {
        return imoviesCall { service.getTopTrailers() }
    }
}