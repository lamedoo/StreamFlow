package com.lukakordzaia.core.domain.usecases

import com.lukakordzaia.core.baseclasses.usecases.BaseResultUseCase
import com.lukakordzaia.core.domain.domainmodels.GithubReleaseModel
import com.lukakordzaia.core.domain.repository.releaserepository.ReleaseRepository
import com.lukakordzaia.core.network.ResultDomain
import com.lukakordzaia.core.network.models.github.response.GetGithubReleasesResponse
import com.lukakordzaia.core.network.transformToModel

class GithubReleasesUseCase(
    private val repository: ReleaseRepository
) : BaseResultUseCase<Unit, List<GetGithubReleasesResponse>, GithubReleaseModel>() {
    override suspend fun invoke(args: Unit?): ResultDomain<GithubReleaseModel, String> {
        return returnData(repository.getGithubReleases()) { it.transformToModel()[0] }
    }
}