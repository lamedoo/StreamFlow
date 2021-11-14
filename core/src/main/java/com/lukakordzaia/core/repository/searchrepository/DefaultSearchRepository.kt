package com.lukakordzaia.core.repository.searchrepository

import com.lukakordzaia.core.network.Result
import com.lukakordzaia.core.network.imovies.ImoviesCall
import com.lukakordzaia.core.network.imovies.ImoviesNetwork
import com.lukakordzaia.core.network.models.imovies.response.categories.GetTopFranchisesResponse
import com.lukakordzaia.core.network.models.imovies.response.titles.GetTitlesResponse

class DefaultSearchRepository(private val service: ImoviesNetwork): ImoviesCall(), SearchRepository {
    override suspend fun getSearchTitles(keywords: String, page: Int): Result<GetTitlesResponse> {
        return imoviesCall { service.getSearchTitles(keywords, page) }
    }

    override suspend fun getTopFranchises(): Result<GetTopFranchisesResponse> {
        return imoviesCall { service.getTopFranchises() }
    }
}