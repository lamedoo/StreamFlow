package com.lukakordzaia.core.domain.usecases

import com.lukakordzaia.core.baseclasses.usecases.BaseResultUseCase
import com.lukakordzaia.core.domain.repository.userrepository.UserRepository
import com.lukakordzaia.core.network.ResultDomain
import com.lukakordzaia.core.network.models.imovies.request.user.PostTitleWatchTimeRequestBody
import com.lukakordzaia.core.network.models.imovies.request.user.PostTitleWatchTimeRequestFull
import com.lukakordzaia.core.network.models.imovies.response.user.PostWatchTimeResponse

class TitleWatchTimeUseCase(
    private val repository: UserRepository
) : BaseResultUseCase<PostTitleWatchTimeRequestFull, PostWatchTimeResponse, PostWatchTimeResponse>() {
    override suspend fun invoke(args: PostTitleWatchTimeRequestFull?): ResultDomain<PostWatchTimeResponse, String> {
        return returnData(repository.titleWatchTime(args!!)) { it }
    }
}