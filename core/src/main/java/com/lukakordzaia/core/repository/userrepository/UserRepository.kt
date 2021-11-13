package com.lukakordzaia.core.repository.userrepository

import com.lukakordzaia.core.network.Result
import com.lukakordzaia.core.network.models.imovies.request.user.PostLoginBody
import com.lukakordzaia.core.network.models.imovies.request.user.PostTitleWatchTimeRequestBody
import com.lukakordzaia.core.network.models.imovies.response.user.GetUserDataResponse
import com.lukakordzaia.core.network.models.imovies.response.user.GetUserLogoutResponse
import com.lukakordzaia.core.network.models.imovies.response.user.PostUserLoginResponse
import com.lukakordzaia.core.network.models.imovies.response.user.PostWatchTimeResponse

interface UserRepository {
    suspend fun userLogin(loginBody: PostLoginBody) : Result<PostUserLoginResponse>
    suspend fun userData() : Result<GetUserDataResponse>
    suspend fun userLogout() : Result<GetUserLogoutResponse>
    suspend fun titleWatchTime(watchTimeRequest: PostTitleWatchTimeRequestBody, titleId: Int, season: Int, episode: Int) : Result<PostWatchTimeResponse>
}