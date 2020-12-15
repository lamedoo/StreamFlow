package com.lukakordzaia.imoviesapp.network


import com.lukakordzaia.imoviesapp.network.models.MovieData
import com.lukakordzaia.imoviesapp.network.models.MovieFiles
import com.lukakordzaia.imoviesapp.network.models.MoviesList
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface MoviesNetwork {

//    Top Movies - movies/top?type=movie&period=day&page=1&per_page=10

    @Headers("User-Agent: imovies")
    @GET ("movies?page=1&per_page=15&filters%5Btype%5D=movie&filters%5Bwith_files%5D=yes&sort=-upload_date")
    suspend fun getMovies() : Response<MoviesList>

    @Headers("User-Agent: imovies")
    @GET ("movies?page=3&per_page=15&filters%5Btype%5D=series&filters%5Bwith_files%5D=yes&sort=-upload_date")
    suspend fun getTvShows() : Response<MoviesList>

    @Headers("User-Agent: imovies")
    @GET ("movies/{id}/")
    suspend fun getSingleMovie(@Path("id") id: Int) : Response<MovieData>

    @Headers("User-Agent: imovies")
    @GET ("movies/{id}/season-files/{season_number}")
    suspend fun getSingleFiles(@Path("id") id: Int, @Path("season_number") season_number: Int) : Response<MovieFiles>
}