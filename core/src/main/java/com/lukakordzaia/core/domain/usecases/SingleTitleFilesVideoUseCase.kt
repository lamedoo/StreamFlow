package com.lukakordzaia.core.domain.usecases

import com.lukakordzaia.core.baseclasses.usecases.BaseResultUseCase
import com.lukakordzaia.core.domain.domainmodels.EpisodeInfoModel
import com.lukakordzaia.core.domain.domainmodels.SeasonEpisodesModel
import com.lukakordzaia.core.domain.repository.singletitlerepository.SingleTitleRepository
import com.lukakordzaia.core.network.ResultDomain
import com.lukakordzaia.core.network.models.imovies.response.singletitle.GetSingleTitleFilesResponse
import com.lukakordzaia.core.network.toEpisodeInfoModel
import com.lukakordzaia.core.network.toSeasonEpisodesModel

class SingleTitleFilesVideoUseCase(
    private val repository: SingleTitleRepository
) : BaseResultUseCase<Pair<Int, Int>, GetSingleTitleFilesResponse, List<GetSingleTitleFilesResponse.Data>>() {
    override suspend fun invoke(args: Pair<Int, Int>?): ResultDomain<List<GetSingleTitleFilesResponse.Data>, String> {
        return returnData(repository.getSingleTitleFiles(args!!.first, args.second)) { it.data }
    }
}