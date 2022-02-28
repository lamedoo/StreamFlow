package com.lukakordzaia.core.domain.repository.watchlistrepository

import com.lukakordzaia.core.network.ResultData
import com.lukakordzaia.core.network.imovies.ImoviesCall
import com.lukakordzaia.core.network.imovies.ImoviesNetwork
import com.lukakordzaia.core.network.models.imovies.response.user.GetUserWatchlistResponse
import com.lukakordzaia.core.network.models.imovies.response.user.UserWatchListStatusResponse

class DefaultWatchlistRepository(private val service: ImoviesNetwork): ImoviesCall(), WatchlistRepository {
    override suspend fun getUserWatchlist(page: Int, type: String): ResultData<GetUserWatchlistResponse> {
        return imoviesCall { service.getUserWatchlistByType(page, type) }
    }

    override suspend fun addWatchlistTitle(id: Int): ResultData<UserWatchListStatusResponse> {
        return imoviesCall { service.addWatchlistTitle(id) }
    }

    override suspend fun deleteWatchlistTitle(id: Int): ResultData<UserWatchListStatusResponse> {
        return imoviesCall { service.deleteWatchlistTitle(id) }
    }
}