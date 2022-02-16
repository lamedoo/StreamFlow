package com.lukakordzaia.core.domain.repository.cataloguerepository

import com.lukakordzaia.core.network.ResultData
import com.lukakordzaia.core.network.models.imovies.response.categories.GetGenresResponse
import com.lukakordzaia.core.network.models.imovies.response.categories.GetTopStudiosResponse
import com.lukakordzaia.core.network.models.imovies.response.titles.GetTitlesResponse

interface CatalogueRepository {
    suspend fun getAllGenres(): ResultData<GetGenresResponse>
    suspend fun getSingleGenre(genreId: Int, page: Int): ResultData<GetTitlesResponse>
    suspend fun getTopStudios(): ResultData<GetTopStudiosResponse>
    suspend fun getSingleStudio(studioId: Int, page: Int): ResultData<GetTitlesResponse>
    suspend fun getTopTrailers(): ResultData<GetTitlesResponse>
}