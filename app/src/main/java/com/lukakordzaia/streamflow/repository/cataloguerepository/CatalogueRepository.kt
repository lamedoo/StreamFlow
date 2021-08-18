package com.lukakordzaia.streamflow.repository.cataloguerepository

import com.lukakordzaia.streamflow.network.Result
import com.lukakordzaia.streamflow.network.models.imovies.response.categories.GetGenresResponse
import com.lukakordzaia.streamflow.network.models.imovies.response.categories.GetTopStudiosResponse
import com.lukakordzaia.streamflow.network.models.imovies.response.titles.GetTitlesResponse

interface CatalogueRepository {
    suspend fun getAllGenres(): Result<GetGenresResponse>
    suspend fun getSingleGenre(genreId: Int, page: Int): Result<GetTitlesResponse>
    suspend fun getTopStudios(): Result<GetTopStudiosResponse>
    suspend fun getSingleStudio(studioId: Int, page: Int): Result<GetTitlesResponse>
    suspend fun getTopTrailers(): Result<GetTitlesResponse>
}