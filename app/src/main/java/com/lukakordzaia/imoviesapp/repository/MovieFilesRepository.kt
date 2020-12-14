package com.lukakordzaia.imoviesapp.repository

import com.lukakordzaia.imoviesapp.network.MoviesNetwork
import com.lukakordzaia.imoviesapp.network.Result
import com.lukakordzaia.imoviesapp.network.ServiceBuilder
import com.lukakordzaia.imoviesapp.network.models.MovieFiles

class MovieFilesRepository {
    private val destinationService = ServiceBuilder.buildService(MoviesNetwork::class.java)

    suspend fun getSingleMovieFiles(movieId: Int, season_number: Int = 1): Result<MovieFiles> {
        return ServiceBuilder.retrofitCall { destinationService.getSingleFiles(movieId, season_number) }
    }
}