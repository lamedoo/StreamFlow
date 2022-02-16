package com.lukakordzaia.core.domain.repository.cataloguerepository

import com.lukakordzaia.core.network.ResultData
import com.lukakordzaia.core.network.imovies.ImoviesCall
import com.lukakordzaia.core.network.imovies.ImoviesNetwork
import com.lukakordzaia.core.network.models.imovies.response.categories.GetGenresResponse
import com.lukakordzaia.core.network.models.imovies.response.categories.GetTopStudiosResponse
import com.lukakordzaia.core.network.models.imovies.response.titles.GetTitlesResponse

class DefaultCatalogueRepository(private val service: ImoviesNetwork): ImoviesCall(), CatalogueRepository {
    override suspend fun getAllGenres(): ResultData<GetGenresResponse> {
        return imoviesCall { service.getAllGenres() }
    }

    override suspend fun getSingleGenre(genreId: Int, page: Int): ResultData<GetTitlesResponse> {
        return imoviesCall { service.getSingleGenre(genreId, page) }
    }

    override suspend fun getTopStudios(): ResultData<GetTopStudiosResponse> {
        return imoviesCall { service.getTopStudios() }
    }

    override suspend fun getSingleStudio(studioId: Int, page: Int): ResultData<GetTitlesResponse> {
        return imoviesCall { service.getSingleStudio(studioId, page) }
    }

    override suspend fun getTopTrailers(): ResultData<GetTitlesResponse> {
        return imoviesCall { service.getTopTrailers() }
    }
}