package com.lukakordzaia.streamflow.repository.userrepository

import com.lukakordzaia.streamflow.network.Result
import com.lukakordzaia.streamflow.network.imovies.ImoviesCall
import com.lukakordzaia.streamflow.network.imovies.ImoviesNetwork
import com.lukakordzaia.streamflow.network.models.imovies.request.user.PostLoginBody
import com.lukakordzaia.streamflow.network.models.imovies.response.user.GetUserDataResponse
import com.lukakordzaia.streamflow.network.models.imovies.response.user.GetUserLogoutResponse
import com.lukakordzaia.streamflow.network.models.imovies.response.user.PostUserLoginResponse

class DefaultUserRepository(private val service: ImoviesNetwork): ImoviesCall(), UserRepository {
    override suspend fun userLogin(loginBody: PostLoginBody): Result<PostUserLoginResponse> {
        return imoviesCall { service.postUserLogin(loginBody) }
    }

    override suspend fun userData(): Result<GetUserDataResponse> {
        return imoviesCall { service.getUserData() }
    }

    override suspend fun userLogout(): Result<GetUserLogoutResponse> {
        return imoviesCall { service.postUserLogOut() }
    }
}