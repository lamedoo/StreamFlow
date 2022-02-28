package com.lukakordzaia.core.domain.usecases

import com.lukakordzaia.core.baseclasses.usecases.BaseResultUseCase
import com.lukakordzaia.core.domain.repository.userrepository.UserRepository
import com.lukakordzaia.core.network.ResultDomain
import com.lukakordzaia.core.network.models.imovies.request.user.PostLoginBody
import com.lukakordzaia.core.network.models.imovies.response.user.GetUserLogoutResponse
import com.lukakordzaia.core.network.models.imovies.response.user.PostUserLoginResponse

class UserLogOutUseCase(
    private val repository: UserRepository
) : BaseResultUseCase<Unit, GetUserLogoutResponse, GetUserLogoutResponse>() {
    override suspend fun invoke(args: Unit?): ResultDomain<GetUserLogoutResponse, String> {
        return returnData(repository.userLogout()) { it }
    }
}