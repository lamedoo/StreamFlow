package com.lukakordzaia.core.domain.repository.releaserepository

import com.lukakordzaia.core.network.ResultData
import com.lukakordzaia.core.network.github.GithubNetwork
import com.lukakordzaia.core.network.imovies.ImoviesCall
import com.lukakordzaia.core.network.models.github.response.GetGithubReleasesResponse

class DefaultReleaseRepository(private val service: GithubNetwork): ImoviesCall(), ReleaseRepository {
    override suspend fun getGithubReleases(): ResultData<List<GetGithubReleasesResponse>> {
        return imoviesCall { service.getGithubReleases() }
    }
}