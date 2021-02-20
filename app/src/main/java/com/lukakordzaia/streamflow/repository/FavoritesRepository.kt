package com.lukakordzaia.streamflow.repository

import com.lukakordzaia.streamflow.datamodels.SingleTitleData
import com.lukakordzaia.streamflow.datamodels.TitleList
import com.lukakordzaia.streamflow.network.Result
import com.lukakordzaia.streamflow.network.RetrofitBuilder
import com.lukakordzaia.streamflow.network.imovies.ImoviesCall

class FavoritesRepository(retrofitBuilder: RetrofitBuilder): ImoviesCall() {
    private val service = retrofitBuilder.buildImoviesService()

    suspend fun getSearchFavoriteTitles(keywords: String, page: Int, year: String): Result<TitleList> {
        return imoviesCall { service.getSearchFavoriteTitles(keywords, page, year) }
    }

    suspend fun getSingleTitleData(titleId: Int): Result<SingleTitleData> {
        return imoviesCall { service.getSingleTitle(titleId) }
    }
}