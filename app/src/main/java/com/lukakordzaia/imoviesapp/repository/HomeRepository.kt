package com.lukakordzaia.imoviesapp.repository

import com.lukakordzaia.imoviesapp.network.Result
import com.lukakordzaia.imoviesapp.network.RetrofitBuilder
import com.lukakordzaia.imoviesapp.network.TitlesNetwork
import com.lukakordzaia.imoviesapp.network.datamodels.TitleList

class HomeRepository {
    private val destinationService = RetrofitBuilder.buildService(TitlesNetwork::class.java)

    suspend fun getMovies(): Result<TitleList> {
        return RetrofitBuilder.retrofitCall { destinationService.getMovies() }
    }

    suspend fun getTvShows(): Result<TitleList> {
        return RetrofitBuilder.retrofitCall { destinationService.getTvShows() }
    }
}