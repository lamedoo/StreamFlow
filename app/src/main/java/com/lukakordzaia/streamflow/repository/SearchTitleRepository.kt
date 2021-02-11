package com.lukakordzaia.streamflow.repository

import com.lukakordzaia.streamflow.datamodels.TitleList
import com.lukakordzaia.streamflow.network.Result
import com.lukakordzaia.streamflow.network.RetrofitBuilder
import com.lukakordzaia.streamflow.network.RetrofitCall

class SearchTitleRepository(private val retrofitBuilder: RetrofitBuilder): RetrofitCall() {
    private val service = retrofitBuilder.buildService()

    suspend fun getSearchTitles(keywords: String, page: Int): Result<TitleList> {
        return retrofitCall { service.getSearchTitles(keywords, page) }
    }
}