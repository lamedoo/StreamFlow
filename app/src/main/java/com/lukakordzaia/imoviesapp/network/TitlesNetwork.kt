package com.lukakordzaia.imoviesapp.network


import com.lukakordzaia.imoviesapp.network.datamodels.GenreList
import com.lukakordzaia.imoviesapp.network.datamodels.TitleData
import com.lukakordzaia.imoviesapp.network.datamodels.TitleFiles
import com.lukakordzaia.imoviesapp.network.datamodels.TitleList
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface TitlesNetwork {

    @Headers("User-Agent: imovies")
    @GET ("movies/top?type=movie&period=day&page=1&per_page=15")
    suspend fun getTopMovies() : Response<TitleList>

    @Headers("User-Agent: imovies")
    @GET ("movies/top?type=series&period=day&page=1&per_page=15")
    suspend fun getTopTvShows() : Response<TitleList>

    @Headers("User-Agent: imovies")
    @GET ("genres?page=1&per_page=100")
    suspend fun getAllGenres() : Response<GenreList>

    @Headers("User-Agent: imovies")
    @GET("movies?per_page=10&sort=-year")
    suspend fun getSingleGenre(@Query("filters[genre]") genreId: Int, @Query("page") page: Int) : Response<TitleList>

    @Headers("User-Agent: imovies")
    @GET ("search-advanced?filters%5Btype%5D=movie&per_page=10")
    suspend fun getSearchTitles(@Query("keywords") keywords: String, @Query("page") page: Int) : Response<TitleList>

    @Headers("User-Agent: imovies")
    @GET ("movies/{id}/")
    suspend fun getSingleTitle(@Path("id") id: Int) : Response<TitleData>

    @Headers("User-Agent: imovies")
    @GET ("movies/{id}/season-files/{season_number}")
    suspend fun getSingleFiles(@Path("id") id: Int, @Path("season_number") season_number: Int) : Response<TitleFiles>
}