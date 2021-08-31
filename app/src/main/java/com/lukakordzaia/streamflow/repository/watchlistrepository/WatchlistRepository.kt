package com.lukakordzaia.streamflow.repository.watchlistrepository

import com.lukakordzaia.streamflow.network.Result
import com.lukakordzaia.streamflow.network.models.imovies.response.user.GetUserWatchlistResponse
import com.lukakordzaia.streamflow.network.models.imovies.response.user.UserWatchListStatusResponse

interface WatchlistRepository {
    suspend fun getUserWatchlist(page: Int) : Result<GetUserWatchlistResponse>
    suspend fun addWatchlistTitle(id: Int) : Result<UserWatchListStatusResponse>
    suspend fun deleteWatchlistTitle(id: Int) : Result<UserWatchListStatusResponse>
}