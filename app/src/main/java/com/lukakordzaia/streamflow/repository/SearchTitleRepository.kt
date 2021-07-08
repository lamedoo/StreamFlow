package com.lukakordzaia.streamflow.repository

import com.lukakordzaia.streamflow.network.models.response.categories.GetTopFranchisesResponse
import com.lukakordzaia.streamflow.network.models.response.titles.GetTitlesResponse
import com.lukakordzaia.streamflow.network.Result
import com.lukakordzaia.streamflow.network.RetrofitBuilder
import com.lukakordzaia.streamflow.network.imovies.ImoviesCall
import com.lukakordzaia.streamflow.network.imovies.ImoviesNetwork

class SearchTitleRepository(private val service: ImoviesNetwork): ImoviesCall() {
    suspend fun getSearchTitles(keywords: String, page: Int): Result<GetTitlesResponse> {
        return imoviesCall { service.getSearchTitles(keywords, page) }
    }

    suspend fun getTopFranchises(): Result<GetTopFranchisesResponse> {
        return imoviesCall { service.getTopFranchises() }
    }
}