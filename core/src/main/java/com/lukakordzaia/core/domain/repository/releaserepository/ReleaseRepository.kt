package com.lukakordzaia.core.domain.repository.releaserepository

import com.lukakordzaia.core.network.ResultData
import com.lukakordzaia.core.network.models.github.response.GetGithubReleasesResponse

interface ReleaseRepository {
    suspend fun getGithubReleases(): ResultData<List<GetGithubReleasesResponse>>
}