package com.lukakordzaia.imoviesapp.repository

import com.lukakordzaia.imoviesapp.network.MoviesNetwork
import com.lukakordzaia.imoviesapp.network.Result
import com.lukakordzaia.imoviesapp.network.ServiceBuilder
import com.lukakordzaia.imoviesapp.network.models.MovieData
import com.lukakordzaia.imoviesapp.network.models.MovieFiles

class SingleMovieRepository {
    private val destinationService = ServiceBuilder.buildService(MoviesNetwork::class.java)

    suspend fun getSingleMovieData(movieId: Int): Result<MovieData> {
        return ServiceBuilder.retrofitCall { destinationService.getSingleMovie(movieId) }
    }
}