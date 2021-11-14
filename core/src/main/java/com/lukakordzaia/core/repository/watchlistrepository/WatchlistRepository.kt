package com.lukakordzaia.core.repository.watchlistrepository

import com.lukakordzaia.core.network.Result
import com.lukakordzaia.core.network.models.imovies.response.user.GetUserWatchlistResponse
import com.lukakordzaia.core.network.models.imovies.response.user.UserWatchListStatusResponse

interface WatchlistRepository {
    suspend fun getUserWatchlist(page: Int, type: String) : Result<GetUserWatchlistResponse>
    suspend fun addWatchlistTitle(id: Int) : Result<UserWatchListStatusResponse>
    suspend fun deleteWatchlistTitle(id: Int) : Result<UserWatchListStatusResponse>
}