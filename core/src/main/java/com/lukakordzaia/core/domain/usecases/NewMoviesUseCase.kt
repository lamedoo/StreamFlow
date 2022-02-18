package com.lukakordzaia.core.domain.usecases

import com.lukakordzaia.core.baseclasses.usecases.BaseResultUseCase
import com.lukakordzaia.core.domain.domainmodels.SingleTitleModel
import com.lukakordzaia.core.domain.repository.homerepistory.HomeRepository
import com.lukakordzaia.core.network.ResultDomain
import com.lukakordzaia.core.network.models.imovies.response.titles.GetTitlesResponse
import com.lukakordzaia.core.network.toTitleListModel

class NewMoviesUseCase(
    private val repository: HomeRepository
) : BaseResultUseCase<Int, GetTitlesResponse, List<SingleTitleModel>, ResultDomain<List<SingleTitleModel>, String>>() {
    override suspend fun invoke(args: Int?): ResultDomain<List<SingleTitleModel>, String> {
        return returnData(repository.getNewMovies(args ?: 1)) { it.data.toTitleListModel() }
    }
}