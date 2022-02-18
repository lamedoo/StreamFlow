package com.lukakordzaia.core.domain.usecases

import com.lukakordzaia.core.baseclasses.usecases.BaseResultUseCase
import com.lukakordzaia.core.domain.repository.singletitlerepository.SingleTitleRepository
import com.lukakordzaia.core.network.ResultDomain
import com.lukakordzaia.core.network.models.imovies.response.singletitle.GetSingleTitleCastResponse
import com.lukakordzaia.core.network.models.imovies.response.singletitle.GetSingleTitleResponse

class SingleTitleCastUseCase(
    private val repository: SingleTitleRepository
) : BaseResultUseCase<Pair<Int, String>, GetSingleTitleCastResponse, GetSingleTitleCastResponse>() {
    override suspend fun invoke(args: Pair<Int, String>?): ResultDomain<GetSingleTitleCastResponse, String> {
        return returnData(repository.getSingleTitleCast(args!!.first, args.second)) { it }
    }
}