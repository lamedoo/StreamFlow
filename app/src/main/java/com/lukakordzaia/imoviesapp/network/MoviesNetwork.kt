package com.lukakordzaia.imoviesapp.network


import com.lukakordzaia.imoviesapp.network.models.MovieData
import com.lukakordzaia.imoviesapp.network.models.MovieFiles
import com.lukakordzaia.imoviesapp.network.models.MoviesList
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface MoviesNetwork {

    //&filters%5Btype%5D=series&sort=-upload_date

    @Headers("User-Agent: imovies")
    @GET ("https://api.imovies.cc/api/v1/movies?page=1&per_page=10&filters%5Btype%5D=series&sort=-upload_date")
    suspend fun getMovies() : Response<MoviesList>

    @Headers("User-Agent: imovies")
    @GET ("https://api.imovies.cc/api/v1/movies/{id}/")
    suspend fun getSingleMovie(@Path("id") id: Int) : Response<MovieData>

    @Headers("User-Agent: imovies")
    @GET ("https://api.imovies.cc/api/v1/movies/{id}/season-files/{season_number}")
    suspend fun getSingleFiles(@Path("id") id: Int, @Path("season_number") season_number: Int) : Response<MovieFiles>
}