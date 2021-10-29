package com.lukakordzaia.streamflow.repository.homerepistory

import com.lukakordzaia.streamflow.network.Result
import com.lukakordzaia.streamflow.network.imovies.ImoviesCall
import com.lukakordzaia.streamflow.network.imovies.ImoviesNetwork
import com.lukakordzaia.streamflow.network.models.imovies.response.newseries.GetNewSeriesResponse
import com.lukakordzaia.streamflow.network.models.imovies.response.titles.GetTitlesResponse
import com.lukakordzaia.streamflow.network.models.imovies.response.user.GetContinueWatchingResponse
import com.lukakordzaia.streamflow.network.models.imovies.response.user.UserWatchListStatusResponse

class DefaultHomeRepository(private val service: ImoviesNetwork): ImoviesCall(), HomeRepository {
    override suspend fun getMovieDay(): Result<GetTitlesResponse> {
        return imoviesCall { service.getMovieDay() }
    }

    override suspend fun getContinueWatching(): Result<GetContinueWatchingResponse> {
        return imoviesCall { service.getContinueWatching() }
    }

    override suspend fun hideTitleContinueWatching(id: Int): Result<UserWatchListStatusResponse> {
        return imoviesCall { service.hideTitleContinueWatching(id) }
    }

    override suspend fun getNewMovies(page: Int): Result<GetTitlesResponse> {
        return imoviesCall { service.getNewMovies(page) }
    }

    override suspend fun getTopMovies(page: Int): Result<GetTitlesResponse> {
        return imoviesCall { service.getTopMovies(page) }
    }

    override suspend fun getTopTvShows(page: Int): Result<GetTitlesResponse> {
        return imoviesCall { service.getTopTvShows(page) }
    }

    override suspend fun getNewSeries(page: Int): Result<GetNewSeriesResponse> {
        return imoviesCall { service.getNewSeries(page) }
    }

    override suspend fun getUserSuggestions(user: Int): Result<GetTitlesResponse> {
        return imoviesCall { service.getUserSuggestions(user) }
    }
}