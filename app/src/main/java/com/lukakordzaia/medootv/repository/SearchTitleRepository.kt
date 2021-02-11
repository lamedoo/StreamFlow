package com.lukakordzaia.medootv.repository

import com.lukakordzaia.medootv.datamodels.TitleList
import com.lukakordzaia.medootv.network.Result
import com.lukakordzaia.medootv.network.RetrofitBuilder
import com.lukakordzaia.medootv.network.RetrofitCall

class SearchTitleRepository(private val retrofitBuilder: RetrofitBuilder): RetrofitCall() {
    private val service = retrofitBuilder.buildService()

    suspend fun getSearchTitles(keywords: String, page: Int): Result<TitleList> {
        return retrofitCall { service.getSearchTitles(keywords, page) }
    }
}