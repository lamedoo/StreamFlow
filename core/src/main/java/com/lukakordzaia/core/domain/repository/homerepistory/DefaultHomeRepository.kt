package com.lukakordzaia.core.domain.repository.homerepistory

import com.lukakordzaia.core.network.ResultData
import com.lukakordzaia.core.network.imovies.ImoviesCall
import com.lukakordzaia.core.network.imovies.ImoviesNetwork
import com.lukakordzaia.core.network.models.imovies.response.newseries.GetNewSeriesResponse
import com.lukakordzaia.core.network.models.imovies.response.titles.GetTitlesResponse
import com.lukakordzaia.core.network.models.imovies.response.user.GetContinueWatchingResponse
import com.lukakordzaia.core.network.models.imovies.response.user.UserWatchListStatusResponse

class DefaultHomeRepository(private val service: ImoviesNetwork): ImoviesCall(), HomeRepository {
    override suspend fun getMovieDay(): ResultData<GetTitlesResponse> {
        return imoviesCall { service.getMovieDay() }
    }

    override suspend fun getContinueWatching(): ResultData<GetContinueWatchingResponse> {
        return imoviesCall { service.getContinueWatching() }
    }

    override suspend fun hideTitleContinueWatching(id: Int): ResultData<UserWatchListStatusResponse> {
        return imoviesCall { service.hideTitleContinueWatching(id) }
    }

    override suspend fun getNewMovies(page: Int?): ResultData<GetTitlesResponse> {
        return imoviesCall { service.getNewMovies(page ?: 1) }
    }

    override suspend fun getTopMovies(page: Int): ResultData<GetTitlesResponse> {
        return imoviesCall { service.getTopMovies(page) }
    }

    override suspend fun getTopTvShows(page: Int): ResultData<GetTitlesResponse> {
        return imoviesCall { service.getTopTvShows(page) }
    }

    override suspend fun getNewSeries(page: Int): ResultData<GetNewSeriesResponse> {
        return imoviesCall { service.getNewSeries(page) }
    }

    override suspend fun getUserSuggestions(user: Int): ResultData<GetTitlesResponse> {
        return imoviesCall { service.getUserSuggestions(user) }
    }
}