package com.lukakordzaia.core.domain.usecases

import com.lukakordzaia.core.baseclasses.usecases.BaseResultUseCase
import com.lukakordzaia.core.domain.repository.cataloguerepository.CatalogueRepository
import com.lukakordzaia.core.network.ResultDomain
import com.lukakordzaia.core.network.models.imovies.response.categories.GetGenresResponse

class AllGenresUseCase(
    private val repository: CatalogueRepository
) : BaseResultUseCase<Unit, GetGenresResponse, List<GetGenresResponse.Data>>() {
    override suspend fun invoke(args: Unit?): ResultDomain<List<GetGenresResponse.Data>, String> {
        return returnData(repository.getAllGenres()) { it.data }
    }
}