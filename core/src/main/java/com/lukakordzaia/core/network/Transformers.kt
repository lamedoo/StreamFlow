package com.lukakordzaia.core.network

import com.lukakordzaia.core.domain.domainmodels.ContinueWatchingModel
import com.lukakordzaia.core.domain.domainmodels.EpisodeInfoModel
import com.lukakordzaia.core.domain.domainmodels.NewSeriesModel
import com.lukakordzaia.core.domain.domainmodels.SingleTitleModel
import com.lukakordzaia.core.network.models.imovies.response.newseries.GetNewSeriesResponse
import com.lukakordzaia.core.network.models.imovies.response.singletitle.GetSingleTitleFilesResponse
import com.lukakordzaia.core.network.models.imovies.response.singletitle.GetSingleTitleResponse
import com.lukakordzaia.core.network.models.imovies.response.titles.GetTitlesResponse
import com.lukakordzaia.core.network.models.imovies.response.user.GetContinueWatchingResponse
import com.lukakordzaia.core.network.models.imovies.response.user.GetUserWatchlistResponse

fun List<GetTitlesResponse.Data>.toTitleListModel(): List<SingleTitleModel> {
    return map {
        SingleTitleModel(
            id = it.id,
            isTvShow = it.isTvShow ?: false,
            displayName = if (it.primaryName.isNotEmpty()) it.primaryName else it.secondaryName,
            nameGeo = it.primaryName,
            nameEng = it.secondaryName,
            poster = it.posters?.data?.x240,
            cover = it.covers?.data?.x1050,
            description = null,
            imdbId = null,
            imdbScore = null,
            releaseYear = it.year.toString(),
            duration = null,
            seasonNum = null,
            country = null,
            trailer = if (it.trailers?.data?.isNotEmpty() == true) it.trailers.data[0]?.fileUrl else null,
            watchlist = it.userWantsToWatch?.data?.status,
            titleDuration = it.userWatch?.data?.duration,
            watchedDuration = it.userWatch?.data?.progress,
            currentSeason = it.userWatch?.data?.season,
            currentEpisode = it.userWatch?.data?.episode,
            currentLanguage = it.userWatch?.data?.language,
            visibility = it.userWatch?.data?.visible
        )
    }
}

fun GetSingleTitleResponse.toSingleTitleModel(): SingleTitleModel {
    val title = this.data

    return SingleTitleModel(
        id = title.id,
        isTvShow = title.isTvShow,
        displayName = if (title.primaryName.isNotEmpty()) title.primaryName else title.secondaryName,
        nameGeo = if (title.primaryName.isNotEmpty()) title.primaryName else "N/A",
        nameEng = if (title.secondaryName.isNotEmpty()) title.secondaryName else "N/A",
        poster = title.posters.data?.x240,
        cover = title.covers?.data?.x1050,
        description = if (title.plot.data.description.isNotEmpty()) title.plot.data.description else "აღწერა არ მოიძებნა",
        imdbId = title.imdbUrl.substring(27, title.imdbUrl.length),
        imdbScore = if (title.rating.imdb != null) title.rating.imdb.score.toString() else "N/A",
        releaseYear = title.year.toString(),
        duration = "${title.duration} წ.",
        seasonNum = if (title.seasons != null) {
            if (title.seasons.data.isNotEmpty()) title.seasons.data.size else 0
        } else {
            0
        },
        country = if (title.countries.data.isNotEmpty()) title.countries.data[0].secondaryName else "N/A",
        trailer = if (title.trailers.data.isNotEmpty()) title.trailers.data[0].fileUrl else null,
        watchlist = title.userWantsToWatch?.data?.status,
        titleDuration = title.userWatch?.data?.duration,
        watchedDuration = title.userWatch?.data?.progress,
        currentSeason = title.userWatch?.data?.season,
        currentEpisode = title.userWatch?.data?.episode,
        currentLanguage = title.userWatch?.data?.language,
        visibility = title.userWatch?.data?.visible
    )
}

fun List<GetUserWatchlistResponse.Data>.toWatchListModel(): List<SingleTitleModel> {
    return map { data ->
        val it = data.movie.data

        SingleTitleModel(
            id = it.id,
            isTvShow = it.isTvShow?: false,
            displayName = if (it.primaryName.isNotEmpty()) it.primaryName else it.secondaryName,
            nameGeo = it.primaryName,
            nameEng = it.secondaryName,
            poster = it.posters?.data?.x240,
            cover = it.covers?.data?.x1050,
            description = null,
            imdbId = null,
            imdbScore = null,
            releaseYear = it.year.toString(),
            duration = null,
            seasonNum = null,
            country = null,
            trailer = if (it.trailers?.data?.isNotEmpty() == true) it.trailers.data[0]?.fileUrl else null,
            watchlist = it.userWantsToWatch?.data?.status,
            titleDuration = it.userWatch?.data?.duration,
            watchedDuration = it.userWatch?.data?.progress,
            currentSeason = it.userWatch?.data?.season,
            currentEpisode = it.userWatch?.data?.episode,
            currentLanguage = it.userWatch?.data?.language,
            visibility = it.userWatch?.data?.visible
        )
    }
}

fun List<GetContinueWatchingResponse.Data>.toContinueWatchingModel(): List<ContinueWatchingModel> {
    return map {
        val movie = it.movie.data

        ContinueWatchingModel(
            cover = movie.posters?.data?.x240,
            duration = movie.duration,
            id = movie.id,
            isTvShow = movie.isTvShow,
            primaryName = if (movie.primaryName.isNotEmpty()) movie.primaryName else movie.secondaryName,
            originalName = if (movie.primaryName.isNotEmpty()) movie.primaryName else movie.secondaryName,
            watchedDuration = it.progress,
            titleDuration = it.duration,
            season = it.season,
            episode = it.episode,
            language = it.language
        )
    }
}

fun List<GetNewSeriesResponse.Data.Movies.Data>.toNewSeriesModel(): List<NewSeriesModel> {
    return map {
        val title = it

        NewSeriesModel(
            id = title.id!!,
            isTvShow = title.isTvShow!!,
            displayName = if (!title.primaryName.isNullOrEmpty()) title.primaryName else title.secondaryName,
            nameGeo = if (!title.primaryName.isNullOrEmpty()) title.primaryName else "N/A",
            nameEng = if (!title.secondaryName.isNullOrEmpty()) title.secondaryName else "N/A",
            poster = title.posters?.data?.x240,
            cover = title.covers?.data?.x1050,
            description = if (!title.plot?.data?.description.isNullOrEmpty()) title.plot?.data?.description else "აღწერა არ მოიძებნა",
            imdbId = title.imdbUrl?.substring(27, title.imdbUrl.length),
            imdbScore = if (title.rating?.imdb != null) title.rating.imdb.score.toString() else "N/A",
            releaseYear = title.year.toString(),
            duration = "${title.duration} წ.",
            currentSeason = title.lastUpdatedSeries?.data?.season,
            currentEpisode = title.lastUpdatedSeries?.data?.episode
        )
    }
}

fun GetNewSeriesResponse.toNewSeriesModel(): List<NewSeriesModel> {
    return this.data?.get(0)?.movies?.data!!.map {
        val title = it

        NewSeriesModel(
            id = title.id!!,
            isTvShow = title.isTvShow!!,
            displayName = if (!title.primaryName.isNullOrEmpty()) title.primaryName else title.secondaryName,
            nameGeo = if (!title.primaryName.isNullOrEmpty()) title.primaryName else "N/A",
            nameEng = if (!title.secondaryName.isNullOrEmpty()) title.secondaryName else "N/A",
            poster = title.posters?.data?.x240,
            cover = title.covers?.data?.x1050,
            description = if (!title.plot?.data?.description.isNullOrEmpty()) title.plot?.data?.description else "აღწერა არ მოიძებნა",
            imdbId = title.imdbUrl?.substring(27, title.imdbUrl.length),
            imdbScore = if (title.rating?.imdb != null) title.rating.imdb.score.toString() else "N/A",
            releaseYear = title.year.toString(),
            duration = "${title.duration} წ.",
            currentSeason = title.lastUpdatedSeries?.data?.season,
            currentEpisode = title.lastUpdatedSeries?.data?.episode
        )
    }
}

fun List<GetSingleTitleFilesResponse.Data>.toEpisodeInfoModel(season: Int, chosenLanguage: String): EpisodeInfoModel {
    val data = this[season]
    val files = data.files

    val languages: MutableList<String> = ArrayList()
    val subtitles: MutableList<String> = ArrayList()
    subtitles.add(0, "გათიშვა")

    val episodeUrls: MutableList<EpisodeInfoModel.EpisodeFiles> = ArrayList()
    val subtitleUrls: MutableList<EpisodeInfoModel.EpisodeSubtitles> = ArrayList()

    files.forEach { file ->
        languages.add(file.lang)

        if (file.files.size == 1) {
            episodeUrls.add(EpisodeInfoModel.EpisodeFiles(file.lang, file.files[0].src))
        } else {
            file.files.forEach {
                if (it.quality == "HIGH") {
                    episodeUrls.add(EpisodeInfoModel.EpisodeFiles(file.lang, it.src))
                }
            }
        }

        if (file.lang == chosenLanguage) {
            if (!file.subtitles.isNullOrEmpty()) {
                file.subtitles.forEach {
                    subtitles.add(it!!.lang)
                    subtitleUrls.add(EpisodeInfoModel.EpisodeSubtitles(it.lang, it.url))
                }
            }
        }
    }

    return EpisodeInfoModel(
        totalEpisodes = this.size,
        episodeName = data.title,
        availableLanguages = languages,
        availableSubs = subtitles,
        episodeFiles = episodeUrls,
        episodeSubs = subtitleUrls
    )
}