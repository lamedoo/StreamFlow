package com.lukakordzaia.imoviesapp.repository

import com.lukakordzaia.imoviesapp.network.MoviesNetwork
import com.lukakordzaia.imoviesapp.network.Result
import com.lukakordzaia.imoviesapp.network.ServiceBuilder
import com.lukakordzaia.imoviesapp.network.models.MovieFiles

class VideoPlayerRepository {
    private val destinationService = ServiceBuilder.buildService(MoviesNetwork::class.java)

    suspend fun getSingleMovieFiles(movieId: Int): Result<MovieFiles> {
        return ServiceBuilder.retrofitCall { destinationService.getSingleFiles(movieId) }
    }
}