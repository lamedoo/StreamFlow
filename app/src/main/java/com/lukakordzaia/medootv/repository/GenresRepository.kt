package com.lukakordzaia.medootv.repository

import com.lukakordzaia.medootv.datamodels.GenreList
import com.lukakordzaia.medootv.datamodels.TitleList
import com.lukakordzaia.medootv.network.Result
import com.lukakordzaia.medootv.network.RetrofitBuilder
import com.lukakordzaia.medootv.network.RetrofitCall

class GenresRepository(private val retrofitBuilder: RetrofitBuilder): RetrofitCall() {
    private val service = retrofitBuilder.buildService()

    suspend fun getAllGenres(): Result<GenreList> {
        return retrofitCall { service.getAllGenres() }
    }

    suspend fun getSingleGenre(genreId: Int, page: Int): Result<TitleList> {
        return retrofitCall { service.getSingleGenre(genreId, page) }
    }
}