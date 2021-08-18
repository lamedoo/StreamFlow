package com.lukakordzaia.streamflow.repository.singletitlerepository

import com.lukakordzaia.streamflow.network.Result
import com.lukakordzaia.streamflow.network.models.imovies.response.singletitle.GetSingleTitleCastResponse
import com.lukakordzaia.streamflow.network.models.imovies.response.singletitle.GetSingleTitleFilesResponse
import com.lukakordzaia.streamflow.network.models.imovies.response.singletitle.GetSingleTitleResponse
import com.lukakordzaia.streamflow.network.models.imovies.response.titles.GetTitlesResponse

interface SingleTitleRepository {
    suspend fun getSingleTitleData(titleId: Int): Result<GetSingleTitleResponse>
    suspend fun getSingleTitleFiles(titleId: Int, season_number: Int = 1): Result<GetSingleTitleFilesResponse>
    suspend fun getSingleTitleCast(titleId: Int, role: String): Result<GetSingleTitleCastResponse>
    suspend fun getSingleTitleRelated(titleId: Int): Result<GetTitlesResponse>
}