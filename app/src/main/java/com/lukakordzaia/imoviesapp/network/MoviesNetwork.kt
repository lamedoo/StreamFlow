package com.lukakordzaia.imoviesapp.network


import com.lukakordzaia.imoviesapp.network.models.MovieData
import com.lukakordzaia.imoviesapp.network.models.MovieFiles
import com.lukakordzaia.imoviesapp.network.models.MoviesList
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface MoviesNetwork {

    @Headers("User-Agent: imovies")
    @GET ("https://api.imovies.cc/api/v1/movies/top?type=movie&period=day&page=1&per_page=4")
    suspend fun getMovies() : Response<MoviesList>

    @Headers("User-Agent: imovies")
    @GET ("https://api.imovies.cc/api/v1/movies/{id}/")
    suspend fun getSingleMovie(@Path("id") id: Int) : Response<MovieData>

    @Headers("User-Agent: imovies")
    @GET ("https://api.imovies.cc/api/v1/movies/{id}/season-files")
    suspend fun getSingleFiles(@Path("id") id: Int) : Response<MovieFiles>
}