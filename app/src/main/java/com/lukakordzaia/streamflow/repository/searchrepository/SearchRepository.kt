package com.lukakordzaia.streamflow.repository.searchrepository

import com.lukakordzaia.streamflow.network.Result
import com.lukakordzaia.streamflow.network.models.imovies.response.categories.GetTopFranchisesResponse
import com.lukakordzaia.streamflow.network.models.imovies.response.titles.GetTitlesResponse

interface SearchRepository {
    suspend fun getSearchTitles(keywords: String, page: Int): Result<GetTitlesResponse>
    suspend fun getTopFranchises(): Result<GetTopFranchisesResponse>
}