package com.lukakordzaia.core.domain.usecases

import com.lukakordzaia.core.baseclasses.usecases.BaseResultUseCase
import com.lukakordzaia.core.domain.domainmodels.SingleTitleModel
import com.lukakordzaia.core.domain.repository.homerepistory.HomeRepository
import com.lukakordzaia.core.network.ResultDomain
import com.lukakordzaia.core.network.models.imovies.response.titles.GetTitlesResponse
import com.lukakordzaia.core.network.toTitleListModel

class MovieDayUseCaseBase(
    private val repository: HomeRepository
) : BaseResultUseCase<Unit, GetTitlesResponse, List<SingleTitleModel>, ResultDomain<List<SingleTitleModel>, String>>() {
    override suspend fun invoke(args: Unit?): ResultDomain<List<SingleTitleModel>, String> {
        return returnData(repository.getMovieDay()) { it.data.toTitleListModel() }
    }
}