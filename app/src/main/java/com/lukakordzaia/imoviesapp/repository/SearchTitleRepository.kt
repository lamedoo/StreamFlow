package com.lukakordzaia.imoviesapp.repository

import com.lukakordzaia.imoviesapp.network.Result
import com.lukakordzaia.imoviesapp.network.ServiceBuilder
import com.lukakordzaia.imoviesapp.network.TitlesNetwork
import com.lukakordzaia.imoviesapp.network.models.TitleList

class SearchTitleRepository {
    private val destinationService = ServiceBuilder.buildService(TitlesNetwork::class.java)

    suspend fun getSearchTitles(keywords: String): Result<TitleList> {
        return ServiceBuilder.retrofitCall { destinationService.getSearchTitles(keywords) }
    }
}