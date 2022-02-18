package com.lukakordzaia.core.domain.usecases

import com.lukakordzaia.core.baseclasses.usecases.BaseResultUseCase
import com.lukakordzaia.core.domain.domainmodels.SingleTitleModel
import com.lukakordzaia.core.domain.repository.searchrepository.SearchRepository
import com.lukakordzaia.core.network.ResultDomain
import com.lukakordzaia.core.network.models.imovies.response.titles.GetTitlesResponse
import com.lukakordzaia.core.network.toTitleListModel

class SearchTitleUseCase(
    private val repository: SearchRepository
) : BaseResultUseCase<Pair<String, Int>, GetTitlesResponse, List<SingleTitleModel>>() {
    override suspend fun invoke(args: Pair<String, Int>?): ResultDomain<List<SingleTitleModel>, String> {
        return returnData(repository.getSearchTitles(args!!.first, args.second)) { it.toTitleListModel() }
    }
}