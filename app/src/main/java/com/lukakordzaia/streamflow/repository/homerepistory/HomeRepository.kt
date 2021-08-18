package com.lukakordzaia.streamflow.repository.homerepistory

import com.lukakordzaia.streamflow.network.Result
import com.lukakordzaia.streamflow.network.models.imovies.response.titles.GetTitlesResponse

interface HomeRepository {
    suspend fun getMovieDay(): Result<GetTitlesResponse>
    suspend fun getNewMovies(page: Int): Result<GetTitlesResponse>
    suspend fun getTopMovies(page: Int): Result<GetTitlesResponse>
    suspend fun getTopTvShows(page: Int): Result<GetTitlesResponse>
}