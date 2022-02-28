package com.lukakordzaia.core.domain.usecases

import com.lukakordzaia.core.baseclasses.usecases.BaseResultUseCase
import com.lukakordzaia.core.domain.domainmodels.SingleTitleModel
import com.lukakordzaia.core.domain.repository.homerepistory.HomeRepository
import com.lukakordzaia.core.domain.repository.singletitlerepository.SingleTitleRepository
import com.lukakordzaia.core.network.ResultDomain
import com.lukakordzaia.core.network.models.imovies.response.titles.GetTitlesResponse
import com.lukakordzaia.core.network.toTitleListModel

class SingleTitleRelatedUseCase(
    private val repository: SingleTitleRepository
) : BaseResultUseCase<Int, GetTitlesResponse, List<SingleTitleModel>>() {
    override suspend fun invoke(args: Int?): ResultDomain<List<SingleTitleModel>, String> {
        return returnData(repository.getSingleTitleRelated(args!!)) { it.toTitleListModel() }
    }
}