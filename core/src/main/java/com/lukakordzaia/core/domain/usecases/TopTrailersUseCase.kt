package com.lukakordzaia.core.domain.usecases

import com.lukakordzaia.core.baseclasses.usecases.BaseResultUseCase
import com.lukakordzaia.core.domain.domainmodels.SingleTitleModel
import com.lukakordzaia.core.domain.repository.cataloguerepository.CatalogueRepository
import com.lukakordzaia.core.network.ResultDomain
import com.lukakordzaia.core.network.models.imovies.response.titles.GetTitlesResponse
import com.lukakordzaia.core.network.toTitleListModel

class TopTrailersUseCase(
    private val repository: CatalogueRepository
) : BaseResultUseCase<Unit, GetTitlesResponse, List<SingleTitleModel>, ResultDomain<List<SingleTitleModel>, String>>() {
    override suspend fun invoke(args: Unit?): ResultDomain<List<SingleTitleModel>, String> {
        return returnData(repository.getTopTrailers()) { it.data.toTitleListModel() }
    }
}