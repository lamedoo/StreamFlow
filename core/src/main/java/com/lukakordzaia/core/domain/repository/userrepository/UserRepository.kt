package com.lukakordzaia.core.domain.repository.userrepository

import com.lukakordzaia.core.network.ResultData
import com.lukakordzaia.core.network.models.imovies.request.user.PostLoginBody
import com.lukakordzaia.core.network.models.imovies.request.user.PostTitleWatchTimeRequestBody
import com.lukakordzaia.core.network.models.imovies.request.user.PostTitleWatchTimeRequestFull
import com.lukakordzaia.core.network.models.imovies.response.user.GetUserDataResponse
import com.lukakordzaia.core.network.models.imovies.response.user.GetUserLogoutResponse
import com.lukakordzaia.core.network.models.imovies.response.user.PostUserLoginResponse
import com.lukakordzaia.core.network.models.imovies.response.user.PostWatchTimeResponse

interface UserRepository {
    suspend fun userLogin(loginBody: PostLoginBody): ResultData<PostUserLoginResponse>
    suspend fun userData(): ResultData<GetUserDataResponse>
    suspend fun userLogout(): ResultData<GetUserLogoutResponse>
    suspend fun titleWatchTime(watchTimeRequest: PostTitleWatchTimeRequestFull): ResultData<PostWatchTimeResponse>
}