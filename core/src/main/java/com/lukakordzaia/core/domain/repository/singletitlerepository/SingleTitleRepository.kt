package com.lukakordzaia.core.domain.repository.singletitlerepository

import com.lukakordzaia.core.network.ResultData
import com.lukakordzaia.core.network.models.imovies.response.singletitle.GetSingleTitleCastResponse
import com.lukakordzaia.core.network.models.imovies.response.singletitle.GetSingleTitleFilesResponse
import com.lukakordzaia.core.network.models.imovies.response.singletitle.GetSingleTitleResponse
import com.lukakordzaia.core.network.models.imovies.response.titles.GetTitlesResponse

interface SingleTitleRepository {
    suspend fun getSingleTitleData(titleId: Int): ResultData<GetSingleTitleResponse>
    suspend fun getSingleTitleFiles(titleId: Int, season_number: Int = 1): ResultData<GetSingleTitleFilesResponse>
    suspend fun getSingleTitleCast(titleId: Int, role: String): ResultData<GetSingleTitleCastResponse>
    suspend fun getSingleTitleRelated(titleId: Int): ResultData<GetTitlesResponse>
}