package com.lukakordzaia.imoviesapp.repository

import com.lukakordzaia.imoviesapp.network.MoviesNetwork
import com.lukakordzaia.imoviesapp.network.Result
import com.lukakordzaia.imoviesapp.network.ServiceBuilder
import com.lukakordzaia.imoviesapp.network.models.MovieData
import com.lukakordzaia.imoviesapp.network.models.MoviesList

class MovieRepository {
    private val destinationService = ServiceBuilder.buildService(MoviesNetwork::class.java)

    suspend fun getMovies(): Result<MoviesList> {
        return ServiceBuilder.retrofitCall { destinationService.getMovies() }
    }
}