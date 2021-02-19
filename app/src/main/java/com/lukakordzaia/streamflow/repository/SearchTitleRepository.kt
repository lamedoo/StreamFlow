package com.lukakordzaia.streamflow.repository

import com.lukakordzaia.streamflow.datamodels.FranchiseList
import com.lukakordzaia.streamflow.datamodels.TitleList
import com.lukakordzaia.streamflow.network.Result
import com.lukakordzaia.streamflow.network.RetrofitBuilder
import com.lukakordzaia.streamflow.network.imovies.ImoviesCall

class SearchTitleRepository(retrofitBuilder: RetrofitBuilder): ImoviesCall() {
    private val service = retrofitBuilder.buildImoviesService()

    suspend fun getSearchTitles(keywords: String, page: Int): Result<TitleList> {
        return imoviesCall { service.getSearchTitles(keywords, page) }
    }

    suspend fun getTopFranchises(): Result<FranchiseList> {
        return imoviesCall { service.getTopFranchises() }
    }

    suspend fun getSearchFavoriteTitles(keywords: String, page: Int, year: String): Result<TitleList> {
        return imoviesCall { service.getSearchFavoriteTitles(keywords, page, year) }
    }
}