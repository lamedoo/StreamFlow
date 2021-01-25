package com.lukakordzaia.imoviesapp.repository

import com.lukakordzaia.imoviesapp.network.Result
import com.lukakordzaia.imoviesapp.network.RetrofitBuilder
import com.lukakordzaia.imoviesapp.network.TitlesNetwork
import com.lukakordzaia.imoviesapp.datamodels.TitleData

class SingleTitleRepository {
    private val destinationService = RetrofitBuilder.buildService(TitlesNetwork::class.java)

    suspend fun getSingleTitleData(titleId: Int): Result<TitleData> {
        return RetrofitBuilder.retrofitCall { destinationService.getSingleTitle(titleId) }
    }
}