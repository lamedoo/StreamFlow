package com.lukakordzaia.core.domain.usecases

import com.lukakordzaia.core.baseclasses.usecases.BaseUseCase
import com.lukakordzaia.core.domain.domainmodels.ContinueWatchingModel
import com.lukakordzaia.core.domain.repository.databaserepository.DatabaseRepository
import com.lukakordzaia.core.network.ResultDomain

class DbAllContinueWatchingUseCase(
    private val repository: DatabaseRepository,
    private val singleTitleUseCase: SingleTitleUseCase
) : BaseUseCase<Unit, ResultDomain<List<ContinueWatchingModel>, String>> {
    override suspend fun invoke(args: Unit?): ResultDomain<List<ContinueWatchingModel>, String> {
        val savedTitles = repository.getContinueWatchingFromRoomA()

        val continueWatchingList: MutableList<ContinueWatchingModel> = mutableListOf()
        var error = ""

        savedTitles.map { savedTitle ->
            when (val result = singleTitleUseCase.invoke(savedTitle.titleId)) {
                is ResultDomain.Success -> {
                    val data = result.data
                    continueWatchingList.add(
                        ContinueWatchingModel(
                            poster = data.poster,
                            cover = data.cover,
                            duration = data.duration,
                            id = savedTitle.titleId,
                            isTvShow = savedTitle.isTvShow,
                            primaryName = data.displayName,
                            originalName = data.nameEng,
                            imdbScore = data.imdbScore,
                            releaseYear = data.releaseYear,
                            genres = data.genres,
                            seasonNum = data.seasonNum,
                            watchedDuration = savedTitle.watchedDuration,
                            titleDuration = savedTitle.titleDuration,
                            season = savedTitle.season,
                            episode = savedTitle.episode,
                            language = savedTitle.language
                        )
                    )
                }
                is ResultDomain.Error -> {
                    error = result.exception
                }
            }
        }

        return if (error == "") {
            ResultDomain.Success(continueWatchingList)
        } else {
            ResultDomain.Error(error)
        }
    }
}