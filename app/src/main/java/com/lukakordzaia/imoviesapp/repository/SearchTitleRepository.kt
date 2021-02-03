package com.lukakordzaia.imoviesapp.repository

import com.lukakordzaia.imoviesapp.network.Result
import com.lukakordzaia.imoviesapp.network.RetrofitBuilder
import com.lukakordzaia.imoviesapp.network.TitlesNetwork
import com.lukakordzaia.imoviesapp.datamodels.TitleList
import com.lukakordzaia.imoviesapp.network.RetrofitCall

class SearchTitleRepository(private val retrofitBuilder: RetrofitBuilder): RetrofitCall() {
    private val service = retrofitBuilder.buildService()

    suspend fun getSearchTitles(keywords: String, page: Int): Result<TitleList> {
        return retrofitCall { service.getSearchTitles(keywords, page) }
    }
}