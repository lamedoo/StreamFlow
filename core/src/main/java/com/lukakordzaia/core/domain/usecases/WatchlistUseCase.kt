package com.lukakordzaia.core.domain.usecases

import com.lukakordzaia.core.baseclasses.usecases.BaseResultUseCase
import com.lukakordzaia.core.domain.domainmodels.SingleTitleModel
import com.lukakordzaia.core.domain.repository.watchlistrepository.WatchlistRepository
import com.lukakordzaia.core.network.ResultDomain
import com.lukakordzaia.core.network.models.imovies.response.user.GetUserWatchlistResponse
import com.lukakordzaia.core.network.toWatchListModel

class WatchlistUseCase(
    private val repository: WatchlistRepository
) : BaseResultUseCase<Pair<Int, String>, GetUserWatchlistResponse, List<SingleTitleModel>>() {
    override suspend fun invoke(args: Pair<Int, String>?): ResultDomain<List<SingleTitleModel>, String> {
        return returnData(repository.getUserWatchlist(args!!.first, args.second)) { it.toWatchListModel() }
    }
}