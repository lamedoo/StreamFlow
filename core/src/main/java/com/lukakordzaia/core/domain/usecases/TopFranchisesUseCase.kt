package com.lukakordzaia.core.domain.usecases

import com.lukakordzaia.core.baseclasses.usecases.BaseResultUseCase
import com.lukakordzaia.core.domain.repository.searchrepository.SearchRepository
import com.lukakordzaia.core.network.ResultDomain
import com.lukakordzaia.core.network.models.imovies.response.categories.GetTopFranchisesResponse

class TopFranchisesUseCase(
    private val repository: SearchRepository
) : BaseResultUseCase<Unit, GetTopFranchisesResponse, GetTopFranchisesResponse>() {
    override suspend fun invoke(args: Unit?): ResultDomain<GetTopFranchisesResponse, String> {
        return returnData(repository.getTopFranchises()) { it }
    }
}