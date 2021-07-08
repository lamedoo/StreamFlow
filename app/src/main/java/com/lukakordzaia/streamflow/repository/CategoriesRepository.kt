package com.lukakordzaia.streamflow.repository

import com.lukakordzaia.streamflow.network.models.response.categories.GetGenresResponse
import com.lukakordzaia.streamflow.network.models.response.categories.GetTopStudiosResponse
import com.lukakordzaia.streamflow.network.models.response.titles.GetTitlesResponse
import com.lukakordzaia.streamflow.network.Result
import com.lukakordzaia.streamflow.network.RetrofitBuilder
import com.lukakordzaia.streamflow.network.imovies.ImoviesCall
import com.lukakordzaia.streamflow.network.imovies.ImoviesNetwork

class CategoriesRepository(private val service: ImoviesNetwork): ImoviesCall() {
    suspend fun getAllGenres(): Result<GetGenresResponse> {
        return imoviesCall { service.getAllGenres() }
    }

    suspend fun getSingleGenre(genreId: Int, page: Int): Result<GetTitlesResponse> {
        return imoviesCall { service.getSingleGenre(genreId, page) }
    }

    suspend fun getTopStudios(): Result<GetTopStudiosResponse> {
        return imoviesCall { service.getTopStudios() }
    }

    suspend fun getSingleStudio(studioId: Int, page: Int): Result<GetTitlesResponse> {
        return imoviesCall { service.getSingleStudio(studioId, page) }
    }

    suspend fun getTopTrailers(): Result<GetTitlesResponse> {
        return imoviesCall { service.getTopTrailers() }
    }
}