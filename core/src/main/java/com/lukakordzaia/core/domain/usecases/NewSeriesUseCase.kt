package com.lukakordzaia.core.domain.usecases

import com.lukakordzaia.core.baseclasses.usecases.BaseResultUseCase
import com.lukakordzaia.core.domain.domainmodels.NewSeriesModel
import com.lukakordzaia.core.domain.repository.homerepistory.HomeRepository
import com.lukakordzaia.core.network.ResultDomain
import com.lukakordzaia.core.network.models.imovies.response.newseries.GetNewSeriesResponse
import com.lukakordzaia.core.network.toNewSeriesModel

class NewSeriesUseCase(
    private val repository: HomeRepository
) : BaseResultUseCase<Int, GetNewSeriesResponse, List<NewSeriesModel>, ResultDomain<List<NewSeriesModel>, String>>() {
    override suspend fun invoke(args: Int?): ResultDomain<List<NewSeriesModel>, String> {
        return returnData(repository.getNewSeries(args ?: 1)) { it.toNewSeriesModel() }
    }
}