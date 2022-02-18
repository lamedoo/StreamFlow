package com.lukakordzaia.core.domain.usecases

import com.lukakordzaia.core.baseclasses.usecases.BaseResultUseCase
import com.lukakordzaia.core.domain.domainmodels.ContinueWatchingModel
import com.lukakordzaia.core.domain.repository.homerepistory.HomeRepository
import com.lukakordzaia.core.network.ResultDomain
import com.lukakordzaia.core.network.models.imovies.response.user.GetContinueWatchingResponse
import com.lukakordzaia.core.network.toContinueWatchingModel

class ContinueWatchingUseCase(
    private val repository: HomeRepository
) : BaseResultUseCase<Unit, GetContinueWatchingResponse, List<ContinueWatchingModel>>() {
    override suspend fun invoke(args: Unit?): ResultDomain<List<ContinueWatchingModel>, String> {
        return returnData(repository.getContinueWatching()) { it.data.toContinueWatchingModel() }
    }
}