package com.lukakordzaia.core.domain.repository.searchrepository

import com.lukakordzaia.core.network.ResultData
import com.lukakordzaia.core.network.imovies.ImoviesCall
import com.lukakordzaia.core.network.imovies.ImoviesNetwork
import com.lukakordzaia.core.network.models.imovies.response.categories.GetTopFranchisesResponse
import com.lukakordzaia.core.network.models.imovies.response.titles.GetTitlesResponse

class DefaultSearchRepository(private val service: ImoviesNetwork): ImoviesCall(), SearchRepository {
    override suspend fun getSearchTitles(keywords: String, page: Int): ResultData<GetTitlesResponse> {
        return imoviesCall { service.getSearchTitles(keywords, page) }
    }

    override suspend fun getTopFranchises(): ResultData<GetTopFranchisesResponse> {
        return imoviesCall { service.getTopFranchises() }
    }
}