package com.lukakordzaia.imoviesapp.network


import com.lukakordzaia.imoviesapp.network.models.MovieData
import com.lukakordzaia.imoviesapp.network.models.MoviesList
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers

interface MoviesNetwork {

    @Headers("User-Agent: imovies")
    @GET ("https://api.imovies.cc/api/v1/movies?per_page=2")
    suspend fun getMovies() : Response<MoviesList>
}