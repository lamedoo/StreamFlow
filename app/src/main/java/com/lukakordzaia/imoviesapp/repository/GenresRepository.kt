package com.lukakordzaia.imoviesapp.repository

import com.lukakordzaia.imoviesapp.network.Result
import com.lukakordzaia.imoviesapp.network.RetrofitBuilder
import com.lukakordzaia.imoviesapp.network.TitlesNetwork
import com.lukakordzaia.imoviesapp.network.datamodels.GenreList
import com.lukakordzaia.imoviesapp.network.datamodels.TitleList

class GenresRepository {
    private val retrofit = RetrofitBuilder.buildService(TitlesNetwork::class.java)

    suspend fun getAllGenres(): Result<GenreList> {
        return RetrofitBuilder.retrofitCall { retrofit.getAllGenres() }
    }

    suspend fun getSingleGenre(genreId: Int, page: Int): Result<TitleList> {
        return RetrofitBuilder.retrofitCall { retrofit.getSingleGenre(genreId, page) }
    }
}