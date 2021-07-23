package com.lukakordzaia.streamflow.network.imovies


import com.lukakordzaia.streamflow.network.EndPoints
import com.lukakordzaia.streamflow.network.models.imovies.response.categories.GetTopFranchisesResponse
import com.lukakordzaia.streamflow.network.models.imovies.response.categories.GetGenresResponse
import com.lukakordzaia.streamflow.network.models.imovies.response.categories.GetTopStudiosResponse
import com.lukakordzaia.streamflow.network.models.imovies.response.titles.GetTitlesResponse
import com.lukakordzaia.streamflow.network.models.imovies.response.singletitle.GetSingleTitleResponse
import com.lukakordzaia.streamflow.network.models.imovies.response.singletitle.GetSingleTitleCastResponse
import com.lukakordzaia.streamflow.network.models.imovies.response.singletitle.GetSingleTitleFilesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface ImoviesNetwork {

    @GET(EndPoints.MOVIE_OF_THE_DAY)
    suspend fun getMovieDay() : Response <GetTitlesResponse>

    @GET(EndPoints.NEW_MOVIES)
    suspend fun getNewMovies(@Query("page") page: Int) : Response<GetTitlesResponse>

    @GET(EndPoints.TOP_MOVIES)
    suspend fun getTopMovies(@Query("page") page: Int) : Response<GetTitlesResponse>

    @GET(EndPoints.TOP_TV_SHOWS)
    suspend fun getTopTvShows(@Query("page") page: Int) : Response<GetTitlesResponse>

    @GET (EndPoints.TOP_TRAILERS)
    suspend fun getTopTrailers() : Response<GetTitlesResponse>

    @GET(EndPoints.GENRES)
    suspend fun getAllGenres() : Response<GetGenresResponse>

    @GET(EndPoints.SINGLE_GENRE)
    suspend fun getSingleGenre(@Query("filters[genre]") genreId: Int, @Query("page") page: Int) : Response<GetTitlesResponse>

    @GET (EndPoints.TOP_STUDIOS)
    suspend fun getTopStudios() : Response<GetTopStudiosResponse>

    @GET(EndPoints.SINGLE_STUDIOS)
    suspend fun getSingleStudio(@Query("filters[studio]") genreId: Int, @Query("page") page: Int) : Response<GetTitlesResponse>

    @GET (EndPoints.SEARCH_TITLES)
    suspend fun getSearchTitles(@Query("keywords") keywords: String, @Query("page") page: Int) : Response<GetTitlesResponse>

    @GET (EndPoints.TOP_FRANCHISES)
    suspend fun getTopFranchises() : Response<GetTopFranchisesResponse>

    @GET (EndPoints.SINGLE_TITLE)
    suspend fun getSingleTitle(@Path("id") id: Int) : Response<GetSingleTitleResponse>

    @GET (EndPoints.SINGLE_TITLE_CAST)
    suspend fun getSingleTitleCast(@Path("id") id: Int, @Query("filters[role]") role: String) : Response<GetSingleTitleCastResponse>

    @GET (EndPoints.SINGLE_TITLE_RELATED)
    suspend fun getSingleTitleRelated(@Path("id") id: Int): Response<GetTitlesResponse>

    @GET (EndPoints.SINGLE_TITLE_FILES)
    suspend fun getSingleTitleFiles(@Path("id") id: Int, @Path("season_number") season_number: Int) : Response<GetSingleTitleFilesResponse>

    @GET ("search-advanced?filters%5Btype%5D=movie&per_page=1&movie_filters%5Bgenres_related%5D=no&movie_filters%5Bcountries_related%5D=no")
    suspend fun getSearchFavoriteTitles(@Query("keywords") keywords: String, @Query("page") page: Int, @Query("movie_filters[year_range]") year: String) : Response<GetTitlesResponse>
}