package com.lukakordzaia.core.network.github

import com.lukakordzaia.core.network.models.github.response.GetGithubReleasesResponse
import retrofit2.Response
import retrofit2.http.GET

interface GithubNetwork {
    @GET("repos/lamedoo/streamflow/releases")
    suspend fun getGithubReleases() : Response<List<GetGithubReleasesResponse>>
}