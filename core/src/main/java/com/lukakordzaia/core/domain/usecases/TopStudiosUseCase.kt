package com.lukakordzaia.core.domain.usecases

import com.lukakordzaia.core.baseclasses.usecases.BaseResultUseCase
import com.lukakordzaia.core.domain.repository.cataloguerepository.CatalogueRepository
import com.lukakordzaia.core.network.ResultDomain
import com.lukakordzaia.core.network.models.imovies.response.categories.GetGenresResponse
import com.lukakordzaia.core.network.models.imovies.response.categories.GetTopStudiosResponse

class TopStudiosUseCase(
    private val repository: CatalogueRepository
) : BaseResultUseCase<Unit, GetTopStudiosResponse, List<GetTopStudiosResponse.Data>, ResultDomain<List<GetTopStudiosResponse.Data>, String>>() {
    override suspend fun invoke(args: Unit?): ResultDomain<List<GetTopStudiosResponse.Data>, String> {
        return transformToDomain(repository.getTopStudios()) { it.data }
    }
}