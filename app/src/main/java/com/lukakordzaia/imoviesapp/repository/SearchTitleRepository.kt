package com.lukakordzaia.imoviesapp.repository

import com.lukakordzaia.imoviesapp.network.Result
import com.lukakordzaia.imoviesapp.network.RetrofitBuilder
import com.lukakordzaia.imoviesapp.network.TitlesNetwork
import com.lukakordzaia.imoviesapp.network.datamodels.TitleList

class SearchTitleRepository {
    private val destinationService = RetrofitBuilder.buildService(TitlesNetwork::class.java)

    suspend fun getSearchTitles(keywords: String): Result<TitleList> {
        return RetrofitBuilder.retrofitCall { destinationService.getSearchTitles(keywords) }
    }
}