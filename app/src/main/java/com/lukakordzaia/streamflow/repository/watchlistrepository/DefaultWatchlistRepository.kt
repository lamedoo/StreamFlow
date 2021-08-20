package com.lukakordzaia.streamflow.repository.watchlistrepository

import com.lukakordzaia.streamflow.network.Result
import com.lukakordzaia.streamflow.network.imovies.ImoviesCall
import com.lukakordzaia.streamflow.network.imovies.ImoviesNetwork
import com.lukakordzaia.streamflow.network.models.imovies.response.user.GetUserWatchlistResponse
import com.lukakordzaia.streamflow.network.models.imovies.response.user.UserWatchListStatusResponse

class DefaultWatchlistRepository(private val service: ImoviesNetwork): ImoviesCall(), WatchlistRepository {
    override suspend fun getUserWatchlist(): Result<GetUserWatchlistResponse> {
        return imoviesCall { service.getUserWatchlist() }
    }

    override suspend fun addWatchlistTitle(id: Int): Result<UserWatchListStatusResponse> {
        return imoviesCall { service.addWatchlistTitle(id) }
    }

    override suspend fun deleteWatchlistTitle(id: Int): Result<UserWatchListStatusResponse> {
        return imoviesCall { service.deleteWatchlistTitle(id) }
    }
}