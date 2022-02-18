package com.lukakordzaia.core.domain.usecases

import com.lukakordzaia.core.baseclasses.usecases.BaseResultUseCase
import com.lukakordzaia.core.domain.repository.watchlistrepository.WatchlistRepository
import com.lukakordzaia.core.network.ResultDomain
import com.lukakordzaia.core.network.models.imovies.response.user.UserWatchListStatusResponse

class DeleteWatchlistUseCase(
    private val repository: WatchlistRepository
) : BaseResultUseCase<Int, UserWatchListStatusResponse, UserWatchListStatusResponse, ResultDomain<UserWatchListStatusResponse, String>>() {
    override suspend fun invoke(args: Int?): ResultDomain<UserWatchListStatusResponse, String> {
        return returnData(repository.deleteWatchlistTitle(args!!)) { it }
    }
}