package com.lukakordzaia.core.domain.usecases

import com.lukakordzaia.core.baseclasses.usecases.BaseResultUseCase
import com.lukakordzaia.core.domain.domainmodels.ContinueWatchingModel
import com.lukakordzaia.core.domain.domainmodels.SingleTitleModel
import com.lukakordzaia.core.domain.repository.homerepistory.HomeRepository
import com.lukakordzaia.core.domain.repository.singletitlerepository.SingleTitleRepository
import com.lukakordzaia.core.network.ResultDomain
import com.lukakordzaia.core.network.models.imovies.response.singletitle.GetSingleTitleResponse
import com.lukakordzaia.core.network.models.imovies.response.user.GetContinueWatchingResponse
import com.lukakordzaia.core.network.toContinueWatchingModel
import com.lukakordzaia.core.network.toSingleTitleModel

class SingleTitleUseCase(
    private val repository: SingleTitleRepository
) : BaseResultUseCase<Int, GetSingleTitleResponse, SingleTitleModel, ResultDomain<SingleTitleModel, String>>() {
    override suspend fun invoke(args: Int?): ResultDomain<SingleTitleModel, String> {
        return transformToDomain(repository.getSingleTitleData(args ?: 1)) { it.toSingleTitleModel() }
    }
}