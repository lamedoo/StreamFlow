package com.lukakordzaia.core.domain.usecases

import com.lukakordzaia.core.baseclasses.usecases.BaseResultUseCase
import com.lukakordzaia.core.domain.repository.userrepository.UserRepository
import com.lukakordzaia.core.network.ResultDomain
import com.lukakordzaia.core.network.models.imovies.request.user.PostLoginBody
import com.lukakordzaia.core.network.models.imovies.response.user.PostUserLoginResponse

class UserLoginUseCase(
    private val repository: UserRepository
) : BaseResultUseCase<PostLoginBody, PostUserLoginResponse, PostUserLoginResponse>() {
    override suspend fun invoke(args: PostLoginBody?): ResultDomain<PostUserLoginResponse, String> {
        return returnData(repository.userLogin(args!!)) { it }
    }
}