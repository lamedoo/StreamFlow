package com.lukakordzaia.imoviesapp.repository

import com.lukakordzaia.imoviesapp.network.Result
import com.lukakordzaia.imoviesapp.network.RetrofitBuilder
import com.lukakordzaia.imoviesapp.datamodels.GenreList
import com.lukakordzaia.imoviesapp.datamodels.TitleList
import com.lukakordzaia.imoviesapp.network.RetrofitCall

class GenresRepository(private val retrofitBuilder: RetrofitBuilder): RetrofitCall() {
    private val service = retrofitBuilder.buildService()

    suspend fun getAllGenres(): Result<GenreList> {
        return retrofitCall { service.getAllGenres() }
    }

    suspend fun getSingleGenre(genreId: Int, page: Int): Result<TitleList> {
        return retrofitCall { service.getSingleGenre(genreId, page) }
    }
}