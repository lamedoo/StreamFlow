package com.lukakordzaia.core.network.imovies


import com.lukakordzaia.core.network.EndPoints
import com.lukakordzaia.core.network.models.imovies.request.user.PostLoginBody
import com.lukakordzaia.core.network.models.imovies.request.user.PostTitleWatchTimeRequestBody
import com.lukakordzaia.core.network.models.imovies.response.categories.GetGenresResponse
import com.lukakordzaia.core.network.models.imovies.response.categories.GetTopFranchisesResponse
import com.lukakordzaia.core.network.models.imovies.response.categories.GetTopStudiosResponse
import com.lukakordzaia.core.network.models.imovies.response.newseries.GetNewSeriesResponse
import com.lukakordzaia.core.network.models.imovies.response.singletitle.GetSingleTitleCastResponse
import com.lukakordzaia.core.network.models.imovies.response.singletitle.GetSingleTitleFilesResponse
import com.lukakordzaia.core.network.models.imovies.response.singletitle.GetSingleTitleResponse
import com.lukakordzaia.core.network.models.imovies.response.titles.GetTitlesResponse
import com.lukakordzaia.core.network.models.imovies.response.user.*
import retrofit2.Response
import retrofit2.http.*

interface ImoviesNetwork {

    @POST(EndPoints.USER_LOGIN)
    suspend fun postUserLogin(@Body loginBody: PostLoginBody) : Response<PostUserLoginResponse>

    @PATCH(EndPoints.USER_LOG_OUT)
    suspend fun postUserLogOut() : Response<GetUserLogoutResponse>

    @GET(EndPoints.USER_DATA)
    suspend fun getUserData() : Response<GetUserDataResponse>

    @GET(EndPoints.MOVIE_OF_THE_DAY)
    suspend fun getMovieDay() : Response <GetTitlesResponse>

    @GET(EndPoints.NEW_MOVIES)
    suspend fun getNewMovies(@Query(PAGE) page: Int) : Response<GetTitlesResponse>

    @GET(EndPoints.TOP_MOVIES)
    suspend fun getTopMovies(@Query(PAGE) page: Int) : Response<GetTitlesResponse>

    @GET(EndPoints.TOP_TV_SHOWS)
    suspend fun getTopTvShows(@Query(PAGE) page: Int) : Response<GetTitlesResponse>

    @GET(EndPoints.NEW_SERIES)
    suspend fun getNewSeries(@Query(PAGE) page: Int) : Response<GetNewSeriesResponse>

    @GET (EndPoints.TOP_TRAILERS)
    suspend fun getTopTrailers() : Response<GetTitlesResponse>

    @GET(EndPoints.GENRES)
    suspend fun getAllGenres() : Response<GetGenresResponse>

    @GET(EndPoints.SINGLE_GENRE)
    suspend fun getSingleGenre(@Query("filters[genre]") genreId: Int, @Query(PAGE) page: Int) : Response<GetTitlesResponse>

    @GET (EndPoints.TOP_STUDIOS)
    suspend fun getTopStudios() : Response<GetTopStudiosResponse>

    @GET(EndPoints.SINGLE_STUDIOS)
    suspend fun getSingleStudio(@Query("filters[studio]") genreId: Int, @Query(PAGE) page: Int) : Response<GetTitlesResponse>

    @GET (EndPoints.SEARCH_TITLES)
    suspend fun getSearchTitles(@Query("keywords") keywords: String, @Query(PAGE) page: Int) : Response<GetTitlesResponse>

    @GET (EndPoints.TOP_FRANCHISES)
    suspend fun getTopFranchises() : Response<GetTopFranchisesResponse>

    @GET (EndPoints.SINGLE_TITLE)
    suspend fun getSingleTitle(@Path(ID) id: Int) : Response<GetSingleTitleResponse>

    @GET (EndPoints.SINGLE_TITLE_CAST)
    suspend fun getSingleTitleCast(@Path(ID) id: Int, @Query("filters[role]") role: String) : Response<GetSingleTitleCastResponse>

    @GET (EndPoints.SINGLE_TITLE_RELATED)
    suspend fun getSingleTitleRelated(@Path(ID) id: Int): Response<GetTitlesResponse>

    @GET (EndPoints.SINGLE_TITLE_FILES)
    suspend fun getSingleTitleFiles(@Path(ID) id: Int, @Path("season_number") season_number: Int) : Response<GetSingleTitleFilesResponse>

    @GET(EndPoints.USER_WATCHLIST)
    suspend fun getUserWatchlist(@Query(PAGE) page: Int) : Response<GetUserWatchlistResponse>

    @GET(EndPoints.USER_WATCHLIST)
    suspend fun getUserWatchlistByType(@Query(PAGE) page: Int,
                                       @Query("filters[type]") type: String) : Response<GetUserWatchlistResponse>

    @DELETE(EndPoints.USER_WATCHLIST_STATUS)
    suspend fun deleteWatchlistTitle(@Path(ID) id: Int) : Response<UserWatchListStatusResponse>

    @POST(EndPoints.USER_WATCHLIST_STATUS)
    suspend fun addWatchlistTitle(@Path(ID) id: Int) : Response<UserWatchListStatusResponse>

    @POST(EndPoints.USER_IS_WATCHING)
    suspend fun postTitleWatchTime(@Body watchTimeRequest: PostTitleWatchTimeRequestBody,
                                   @Path(ID) id: Int,
                                   @Path("season") season: Int,
                                   @Path("episode") episode: Int) : Response<PostWatchTimeResponse>

    @GET(EndPoints.USER_CONTINUE_WATCHING)
    suspend fun getContinueWatching() : Response<GetContinueWatchingResponse>

    @PATCH(EndPoints.HIDE_CONTINUE_WATCHING)
    suspend fun hideTitleContinueWatching(@Path(ID) id: Int) : Response<UserWatchListStatusResponse>

    @GET(EndPoints.USER_SUGGESTIONS)
    suspend fun getUserSuggestions(@Query(USER) user: Int) : Response<GetTitlesResponse>
    
    companion object {
        private const val ID = "id"
        private const val PAGE = "page"
        private const val USER = "user"
    }
}