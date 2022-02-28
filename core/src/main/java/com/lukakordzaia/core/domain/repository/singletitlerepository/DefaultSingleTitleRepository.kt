package com.lukakordzaia.core.domain.repository.singletitlerepository

import com.lukakordzaia.core.network.ResultData
import com.lukakordzaia.core.network.imovies.ImoviesCall
import com.lukakordzaia.core.network.imovies.ImoviesNetwork
import com.lukakordzaia.core.network.models.imovies.response.singletitle.GetSingleTitleCastResponse
import com.lukakordzaia.core.network.models.imovies.response.singletitle.GetSingleTitleFilesResponse
import com.lukakordzaia.core.network.models.imovies.response.singletitle.GetSingleTitleResponse
import com.lukakordzaia.core.network.models.imovies.response.titles.GetTitlesResponse

class DefaultSingleTitleRepository(private val service: ImoviesNetwork): ImoviesCall(), SingleTitleRepository {
    override suspend fun getSingleTitleData(titleId: Int): ResultData<GetSingleTitleResponse> {
        return imoviesCall { service.getSingleTitle(titleId) }
    }

    override suspend fun getSingleTitleFiles(titleId: Int, season_number: Int): ResultData<GetSingleTitleFilesResponse> {
        return imoviesCall { service.getSingleTitleFiles(titleId, season_number) }
    }

    override suspend fun getSingleTitleCast(titleId: Int, role: String): ResultData<GetSingleTitleCastResponse> {
        return imoviesCall { service.getSingleTitleCast(titleId, role) }
    }

    override suspend fun getSingleTitleRelated(titleId: Int): ResultData<GetTitlesResponse> {
        return imoviesCall { service.getSingleTitleRelated(titleId) }
    }
}