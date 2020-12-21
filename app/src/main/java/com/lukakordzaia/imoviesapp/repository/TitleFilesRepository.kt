package com.lukakordzaia.imoviesapp.repository

import com.lukakordzaia.imoviesapp.network.Result
import com.lukakordzaia.imoviesapp.network.RetrofitBuilder
import com.lukakordzaia.imoviesapp.network.TitlesNetwork
import com.lukakordzaia.imoviesapp.network.datamodels.TitleFiles

class TitleFilesRepository {
    private val destinationService = RetrofitBuilder.buildService(TitlesNetwork::class.java)

    suspend fun getSingleTitleFiles(titleId: Int, season_number: Int = 1): Result<TitleFiles> {
        return RetrofitBuilder.retrofitCall { destinationService.getSingleFiles(titleId, season_number) }
    }
}