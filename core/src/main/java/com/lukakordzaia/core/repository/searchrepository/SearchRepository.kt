package com.lukakordzaia.core.repository.searchrepository

import com.lukakordzaia.core.network.Result
import com.lukakordzaia.core.network.models.imovies.response.categories.GetTopFranchisesResponse
import com.lukakordzaia.core.network.models.imovies.response.titles.GetTitlesResponse

interface SearchRepository {
    suspend fun getSearchTitles(keywords: String, page: Int): Result<GetTitlesResponse>
    suspend fun getTopFranchises(): Result<GetTopFranchisesResponse>
}