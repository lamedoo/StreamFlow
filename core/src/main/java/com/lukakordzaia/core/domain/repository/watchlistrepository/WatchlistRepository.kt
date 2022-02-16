package com.lukakordzaia.core.domain.repository.watchlistrepository

import com.lukakordzaia.core.network.ResultData
import com.lukakordzaia.core.network.models.imovies.response.user.GetUserWatchlistResponse
import com.lukakordzaia.core.network.models.imovies.response.user.UserWatchListStatusResponse

interface WatchlistRepository {
    suspend fun getUserWatchlist(page: Int, type: String): ResultData<GetUserWatchlistResponse>
    suspend fun addWatchlistTitle(id: Int): ResultData<UserWatchListStatusResponse>
    suspend fun deleteWatchlistTitle(id: Int): ResultData<UserWatchListStatusResponse>
}