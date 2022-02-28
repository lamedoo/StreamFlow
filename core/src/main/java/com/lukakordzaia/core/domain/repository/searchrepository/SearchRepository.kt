package com.lukakordzaia.core.domain.repository.searchrepository

import com.lukakordzaia.core.network.ResultData
import com.lukakordzaia.core.network.models.imovies.response.categories.GetTopFranchisesResponse
import com.lukakordzaia.core.network.models.imovies.response.titles.GetTitlesResponse

interface SearchRepository {
    suspend fun getSearchTitles(keywords: String, page: Int): ResultData<GetTitlesResponse>
    suspend fun getTopFranchises(): ResultData<GetTopFranchisesResponse>
}