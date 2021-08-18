package com.lukakordzaia.streamflow.repository.cataloguerepository

import com.lukakordzaia.streamflow.network.Result
import com.lukakordzaia.streamflow.network.imovies.ImoviesCall
import com.lukakordzaia.streamflow.network.imovies.ImoviesNetwork
import com.lukakordzaia.streamflow.network.models.imovies.response.categories.GetGenresResponse
import com.lukakordzaia.streamflow.network.models.imovies.response.categories.GetTopStudiosResponse
import com.lukakordzaia.streamflow.network.models.imovies.response.titles.GetTitlesResponse

class DefaultCatalogueRepository(private val service: ImoviesNetwork): ImoviesCall(), CatalogueRepository {
    override suspend fun getAllGenres(): Result<GetGenresResponse> {
        return imoviesCall { service.getAllGenres() }
    }

    override suspend fun getSingleGenre(genreId: Int, page: Int): Result<GetTitlesResponse> {
        return imoviesCall { service.getSingleGenre(genreId, page) }
    }

    override suspend fun getTopStudios(): Result<GetTopStudiosResponse> {
        return imoviesCall { service.getTopStudios() }
    }

    override suspend fun getSingleStudio(studioId: Int, page: Int): Result<GetTitlesResponse> {
        return imoviesCall { service.getSingleStudio(studioId, page) }
    }

    override suspend fun getTopTrailers(): Result<GetTitlesResponse> {
        return imoviesCall { service.getTopTrailers() }
    }
}