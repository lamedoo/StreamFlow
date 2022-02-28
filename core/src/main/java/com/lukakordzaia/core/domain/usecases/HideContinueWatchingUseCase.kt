package com.lukakordzaia.core.domain.usecases

import com.lukakordzaia.core.baseclasses.usecases.BaseResultUseCase
import com.lukakordzaia.core.domain.repository.homerepistory.HomeRepository
import com.lukakordzaia.core.network.ResultDomain
import com.lukakordzaia.core.network.models.imovies.response.user.UserWatchListStatusResponse

class HideContinueWatchingUseCase(
    private val repository: HomeRepository
) : BaseResultUseCase<Int, UserWatchListStatusResponse, UserWatchListStatusResponse>() {
    override suspend fun invoke(args: Int?): ResultDomain<UserWatchListStatusResponse, String> {
        return returnData(repository.hideTitleContinueWatching(args!!)) { it }
    }
}