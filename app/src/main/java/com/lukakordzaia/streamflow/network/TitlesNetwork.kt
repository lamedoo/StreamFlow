package com.lukakordzaia.streamflow.network


import com.lukakordzaia.streamflow.datamodels.*
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface TitlesNetwork {

    @Headers("User-Agent: imovies")
    @GET("movies/movie-day?page=1&per_page=1")
    suspend fun getMovieDay() : Response <TitleList>

    @Headers("User-Agent: imovies")
    @GET("movies?filters%5Bwith_files%5D=yes&filters%5Btype%5D=movie&sort=-upload_date&per_page=55")
    suspend fun getNewMovies(@Query("page") page: Int) : Response<TitleList>

    @Headers("User-Agent: imovies")
    @GET ("movies/top?type=movie&period=day&page=1&per_page=55")
    suspend fun getTopMovies(@Query("page") page: Int) : Response<TitleList>

    @Headers("User-Agent: imovies")
    @GET ("movies/top?type=series&period=day&per_page=55")
    suspend fun getTopTvShows(@Query("page") page: Int) : Response<TitleList>

    @Headers("User-Agent: imovies")
    @GET ("genres?page=1&per_page=100")
    suspend fun getAllGenres() : Response<GenreList>

    @Headers("User-Agent: imovies")
    @GET("movies?filters%5Bwith_files%5D=yes&per_page=55&sort=-year")
    suspend fun getSingleGenre(@Query("filters[genre]") genreId: Int, @Query("page") page: Int) : Response<TitleList>

    @Headers("User-Agent: imovies")
    @GET ("studios?page=1&per_page=15&sort=-rating")
    suspend fun getTopStudios() : Response<StudioList>

    @Headers("User-Agent: imovies")
    @GET ("trailers/trailer-day?page=1&per_page=5")
    suspend fun getTopTrailers() : Response<TitleList>

    @Headers("User-Agent: imovies")
    @GET("movies?filters%5Bwith_files%5D=yes&per_page=55&sort=-year")
    suspend fun getSingleStudio(@Query("filters[studio]") genreId: Int, @Query("page") page: Int) : Response<TitleList>

    @Headers("User-Agent: imovies")
    @GET ("search-advanced?filters%5Btype%5D=movie&per_page=25")
    suspend fun getSearchTitles(@Query("keywords") keywords: String, @Query("page") page: Int) : Response<TitleList>

    @Headers("User-Agent: imovies")
    @GET ("franchises?page=1&per_page=12&sort=rand&filters%5Bfeatured%5D=yes")
    suspend fun getTopFranchises() : Response<FranchiseList>

    @Headers("User-Agent: imovies")
    @GET ("movies/{id}/")
    suspend fun getSingleTitle(@Path("id") id: Int) : Response<SingleTitleData>

    @Headers("User-Agent: imovies")
    @GET ("movies/{id}/persons")
    suspend fun getSingleTitleCast(@Path("id") id: Int, @Query("filters[role]") role: String) : Response<TitleCast>

    @Headers("User-Agent: imovies")
    @GET ("movies/{id}/related?page=1&per_page=10")
    suspend fun getSingleTitleRelated(@Path("id") id: Int): Response<TitleList>

    @Headers("User-Agent: imovies")
    @GET ("movies/{id}/season-files/{season_number}")
    suspend fun getSingleFiles(@Path("id") id: Int, @Path("season_number") season_number: Int) : Response<TitleFiles>
}