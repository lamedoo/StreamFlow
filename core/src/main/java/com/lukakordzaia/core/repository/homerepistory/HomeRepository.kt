package com.lukakordzaia.core.repository.homerepistory

import com.lukakordzaia.core.network.Result
import com.lukakordzaia.core.network.models.imovies.response.newseries.GetNewSeriesResponse
import com.lukakordzaia.core.network.models.imovies.response.titles.GetTitlesResponse
import com.lukakordzaia.core.network.models.imovies.response.user.GetContinueWatchingResponse
import com.lukakordzaia.core.network.models.imovies.response.user.UserWatchListStatusResponse

interface HomeRepository {
    suspend fun getMovieDay(): Result<GetTitlesResponse>
    suspend fun getContinueWatching(): Result<GetContinueWatchingResponse>
    suspend fun hideTitleContinueWatching(id: Int): Result<UserWatchListStatusResponse>
    suspend fun getNewMovies(page: Int): Result<GetTitlesResponse>
    suspend fun getTopMovies(page: Int): Result<GetTitlesResponse>
    suspend fun getTopTvShows(page: Int): Result<GetTitlesResponse>
    suspend fun getNewSeries(page: Int): Result<GetNewSeriesResponse>
    suspend fun getUserSuggestions(user: Int): Result<GetTitlesResponse>
}