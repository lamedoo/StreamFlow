package com.lukakordzaia.imoviesapp.repository

import com.lukakordzaia.imoviesapp.network.Result
import com.lukakordzaia.imoviesapp.network.ServiceBuilder
import com.lukakordzaia.imoviesapp.network.TitlesNetwork
import com.lukakordzaia.imoviesapp.network.models.TitleFiles

class TitleFilesRepository {
    private val destinationService = ServiceBuilder.buildService(TitlesNetwork::class.java)

    suspend fun getSingleTitleFiles(titleId: Int, season_number: Int = 1): Result<TitleFiles> {
        return ServiceBuilder.retrofitCall { destinationService.getSingleFiles(titleId, season_number) }
    }
}