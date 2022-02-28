package com.lukakordzaia.core.domain.repository.homerepistory

import com.lukakordzaia.core.network.ResultData
import com.lukakordzaia.core.network.models.imovies.response.newseries.GetNewSeriesResponse
import com.lukakordzaia.core.network.models.imovies.response.titles.GetTitlesResponse
import com.lukakordzaia.core.network.models.imovies.response.user.GetContinueWatchingResponse
import com.lukakordzaia.core.network.models.imovies.response.user.UserWatchListStatusResponse

interface HomeRepository {
    suspend fun getMovieDay(): ResultData<GetTitlesResponse>
    suspend fun getContinueWatching(): ResultData<GetContinueWatchingResponse>
    suspend fun hideTitleContinueWatching(id: Int): ResultData<UserWatchListStatusResponse>
    suspend fun getNewMovies(page: Int): ResultData<GetTitlesResponse>
    suspend fun getTopMovies(page: Int): ResultData<GetTitlesResponse>
    suspend fun getTopTvShows(page: Int): ResultData<GetTitlesResponse>
    suspend fun getNewSeries(page: Int): ResultData<GetNewSeriesResponse>
    suspend fun getUserSuggestions(user: Int): ResultData<GetTitlesResponse>
}