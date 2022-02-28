package com.lukakordzaia.core.domain.usecases

import com.lukakordzaia.core.baseclasses.usecases.BaseResultUseCase
import com.lukakordzaia.core.domain.domainmodels.SeasonEpisodesModel
import com.lukakordzaia.core.domain.repository.singletitlerepository.SingleTitleRepository
import com.lukakordzaia.core.network.ResultDomain
import com.lukakordzaia.core.network.models.imovies.response.singletitle.GetSingleTitleFilesResponse
import com.lukakordzaia.core.network.toSeasonEpisodesModel

class SingleTitleFilesUseCase(
    private val repository: SingleTitleRepository
) : BaseResultUseCase<Pair<Int, Int>, GetSingleTitleFilesResponse, List<SeasonEpisodesModel>>() {
    override suspend fun invoke(args: Pair<Int, Int>?): ResultDomain<List<SeasonEpisodesModel>, String> {
        return returnData(repository.getSingleTitleFiles(args!!.first, args.second)) { it.toSeasonEpisodesModel() }
    }
}