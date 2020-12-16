package com.lukakordzaia.imoviesapp.repository

import com.lukakordzaia.imoviesapp.network.Result
import com.lukakordzaia.imoviesapp.network.ServiceBuilder
import com.lukakordzaia.imoviesapp.network.TitlesNetwork
import com.lukakordzaia.imoviesapp.network.models.TitleData

class SingleTitleRepository {
    private val destinationService = ServiceBuilder.buildService(TitlesNetwork::class.java)

    suspend fun getSingleTitleData(movieId: Int): Result<TitleData> {
        return ServiceBuilder.retrofitCall { destinationService.getSingleTitle(movieId) }
    }
}