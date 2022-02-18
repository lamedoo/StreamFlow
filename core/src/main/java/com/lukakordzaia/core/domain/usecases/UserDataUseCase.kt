package com.lukakordzaia.core.domain.usecases

import com.lukakordzaia.core.baseclasses.usecases.BaseResultUseCase
import com.lukakordzaia.core.domain.repository.userrepository.UserRepository
import com.lukakordzaia.core.network.ResultDomain
import com.lukakordzaia.core.network.models.imovies.request.user.PostLoginBody
import com.lukakordzaia.core.network.models.imovies.response.user.GetUserDataResponse
import com.lukakordzaia.core.network.models.imovies.response.user.PostUserLoginResponse

class UserDataUseCase(
    private val repository: UserRepository
) : BaseResultUseCase<Unit, GetUserDataResponse, GetUserDataResponse>() {
    override suspend fun invoke(args: Unit?): ResultDomain<GetUserDataResponse, String> {
        return returnData(repository.userData()) { it }
    }
}