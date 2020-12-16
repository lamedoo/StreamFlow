package com.lukakordzaia.imoviesapp.repository

import com.lukakordzaia.imoviesapp.network.Result
import com.lukakordzaia.imoviesapp.network.ServiceBuilder
import com.lukakordzaia.imoviesapp.network.TitlesNetwork
import com.lukakordzaia.imoviesapp.network.models.TitleList

class HomeRepository {
    private val destinationService = ServiceBuilder.buildService(TitlesNetwork::class.java)

    suspend fun getMovies(): Result<TitleList> {
        return ServiceBuilder.retrofitCall { destinationService.getMovies() }
    }

    suspend fun getTvShows(): Result<TitleList> {
        return ServiceBuilder.retrofitCall { destinationService.getTvShows() }
    }
}