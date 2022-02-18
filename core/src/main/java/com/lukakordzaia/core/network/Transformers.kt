package com.lukakordzaia.core.network

import com.lukakordzaia.core.datamodels.TvInfoModel
import com.lukakordzaia.core.domain.domainmodels.*
import com.lukakordzaia.core.network.models.imovies.response.newseries.GetNewSeriesResponse
import com.lukakordzaia.core.network.models.imovies.response.singletitle.GetSingleTitleFilesResponse
import com.lukakordzaia.core.network.models.imovies.response.singletitle.GetSingleTitleResponse
import com.lukakordzaia.core.network.models.imovies.response.titles.GetTitlesResponse
import com.lukakordzaia.core.network.models.imovies.response.user.GetContinueWatchingResponse
import com.lukakordzaia.core.network.models.imovies.response.user.GetUserWatchlistResponse

fun List<GetTitlesResponse.Data>.toTitleListModel(): List<SingleTitleModel> {
    return map { data ->
        val genres: MutableList<String> = ArrayList()
        data.genres?.data?.forEach { it.primaryName?.let { name -> genres.add(name) } }

        SingleTitleModel(
            id = data.id,
            isTvShow = data.isTvShow ?: false,
            displayName = data.primaryName.ifEmpty { data.secondaryName },
            nameGeo = data.primaryName,
            nameEng = data.secondaryName,
            poster = data.posters?.data?.x240,
            cover = data.covers?.data?.x1050,
            description = null,
            imdbId = null,
            imdbScore = data.rating.imdb?.let { it.score.toString() } ?: run { "N/A" },
            releaseYear = data.year.toString(),
            genres = genres,
            duration = "${data.duration} წ.",
            seasonNum = data.lastSeries?.data?.season,
            country = null,
            trailer = if (data.trailers?.data?.isNotEmpty() == true) data.trailers.data[0]?.fileUrl else null,
            watchlist = data.userWantsToWatch?.data?.status,
            titleDuration = data.userWatch?.data?.duration,
            watchedDuration = data.userWatch?.data?.progress,
            currentSeason = data.userWatch?.data?.season,
            currentEpisode = data.userWatch?.data?.episode,
            currentLanguage = data.userWatch?.data?.language,
            visibility = data.userWatch?.data?.visible
        )
    }
}

fun GetSingleTitleResponse.toSingleTitleModel(): SingleTitleModel {
    val title = this.data

    val genres: MutableList<String> = ArrayList()
    data.genres.data.forEach { it.primaryName?.let { name -> genres.add(name) } }

    return SingleTitleModel(
        id = title.id,
        isTvShow = title.isTvShow,
        displayName = title.primaryName.ifEmpty { title.secondaryName },
        nameGeo = title.primaryName.ifEmpty { "N/A" },
        nameEng = title.secondaryName.ifEmpty { "N/A" },
        poster = title.posters.data?.x240,
        cover = title.covers?.data?.x1050,
        description = title.plot.data.description.ifEmpty { "აღწერა არ მოიძებნა" },
        imdbId = title.imdbUrl.substring(27, title.imdbUrl.length),
        imdbScore = title.rating.imdb?.let { it.score.toString() } ?: run { "N/A" },
        releaseYear = title.year.toString(),
        genres = genres,
        duration = "${title.duration} წ.",
        seasonNum = title.seasons?.let { if (title.seasons.data.isNotEmpty()) title.seasons.data.size else 0 } ?: run { 0 },
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
            isTvShow = it.isTvShow ?: false,
            displayName = it.primaryName.ifEmpty { it.secondaryName },
            nameGeo = it.primaryName,
            nameEng = it.secondaryName,
            poster = it.posters?.data?.x240,
            cover = it.covers?.data?.x1050,
            description = null,
            imdbId = null,
            imdbScore = it.rating.imdb?.score?.toString() ?: run { "N/A" },
            releaseYear = it.year.toString(),
            genres = null,
            duration = "${it.duration} წ.",
            seasonNum = it.lastSeries?.data?.season,
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
    return map { data ->
        val title = data.movie.data

        val genres: MutableList<String> = ArrayList()
        title.genres.data.forEach { it.primaryName?.let { name -> genres.add(name) } }

        ContinueWatchingModel(
            poster = title.posters?.data?.x240,
            cover = title.covers.data.x1050,
            duration = title.duration,
            id = title.id,
            isTvShow = title.isTvShow,
            primaryName = title.primaryName.ifEmpty { title.secondaryName },
            originalName = title.secondaryName.ifEmpty { title.primaryName },
            imdbScore = title.rating?.imdb?.score?.toString() ?: run { "N/A" },
            releaseYear = title.year.toString(),
            genres = genres,
            seasonNum = data.season,
            watchedDuration = data.progress,
            titleDuration = data.duration,
            season = data.season,
            episode = data.episode,
            language = data.language
        )
    }
}

fun List<GetNewSeriesResponse.Data.Movies.Data>.toNewSeriesModel(): List<NewSeriesModel> {
    return map { data ->

        val genres: MutableList<String> = ArrayList()
        data.genres?.data?.forEach { it?.primaryName?.let { name -> genres.add(name) } }

        NewSeriesModel(
            id = data.id!!,
            isTvShow = data.isTvShow!!,
            displayName = if (!data.primaryName.isNullOrEmpty()) data.primaryName else data.secondaryName,
            nameGeo = if (!data.primaryName.isNullOrEmpty()) data.primaryName else "N/A",
            nameEng = if (!data.secondaryName.isNullOrEmpty()) data.secondaryName else "N/A",
            poster = data.posters?.data?.x240,
            cover = data.covers?.data?.x1050,
            description = if (!data.plot?.data?.description.isNullOrEmpty()) data.plot?.data?.description else "აღწერა არ მოიძებნა",
            imdbId = data.imdbUrl?.substring(27, data.imdbUrl.length),
            imdbScore = data.rating?.imdb?.let { it.score.toString() } ?: run { "N/A" },
            releaseYear = data.year.toString(),
            genres = genres,
            duration = "${data.duration} წ.",
            currentSeason = data.lastUpdatedSeries?.data?.season,
            currentEpisode = data.lastUpdatedSeries?.data?.episode
        )
    }
}

fun GetNewSeriesResponse.toNewSeriesModel(): List<NewSeriesModel> {
    return this.data?.get(0)?.movies?.data!!.map { data ->
        val genres: MutableList<String> = ArrayList()
        data.genres?.data?.forEach { it?.primaryName?.let { name -> genres.add(name) } }

        NewSeriesModel(
            id = data.id!!,
            isTvShow = data.isTvShow!!,
            displayName = if (!data.primaryName.isNullOrEmpty()) data.primaryName else data.secondaryName,
            nameGeo = if (!data.primaryName.isNullOrEmpty()) data.primaryName else "N/A",
            nameEng = if (!data.secondaryName.isNullOrEmpty()) data.secondaryName else "N/A",
            poster = data.posters?.data?.x240,
            cover = data.covers?.data?.x1050,
            description = if (!data.plot?.data?.description.isNullOrEmpty()) data.plot?.data?.description else "აღწერა არ მოიძებნა",
            imdbId = data.imdbUrl?.substring(27, data.imdbUrl.length),
            imdbScore = if (data.rating?.imdb != null) data.rating.imdb.score.toString() else "N/A",
            releaseYear = data.year.toString(),
            genres = genres,
            duration = "${data.duration} წ.",
            currentSeason = data.lastUpdatedSeries?.data?.season,
            currentEpisode = data.lastUpdatedSeries?.data?.episode
        )
    }
}

fun SingleTitleModel.toTvInfoModel(): TvInfoModel {
    return TvInfoModel(
        id = this.id,
        isTvShow = this.isTvShow,
        displayName = this.displayName,
        nameGeo = this.nameGeo,
        nameEng = this.nameEng,
        cover = this.cover,
        imdbScore = this.imdbScore,
        releaseYear = this.releaseYear,
        duration = this.duration,
        seasonNum = this.seasonNum,
        genres = this.genres
    )
}

fun NewSeriesModel.toTvInfoModel(): TvInfoModel {
    return TvInfoModel(
        id = this.id,
        isTvShow = this.isTvShow,
        displayName = this.displayName,
        nameGeo = this.nameGeo,
        nameEng = this.nameEng,
        cover = this.cover,
        imdbScore = this.imdbScore,
        releaseYear = this.releaseYear,
        duration = this.duration,
        seasonNum = this.currentSeason,
        genres = this.genres
    )
}

fun ContinueWatchingModel.toTvInfoModel(): TvInfoModel {
    return TvInfoModel(
        id = this.id,
        isTvShow = this.isTvShow,
        displayName = this.primaryName,
        nameGeo = this.primaryName,
        nameEng = this.originalName,
        cover = this.cover,
        imdbScore = this.imdbScore,
        releaseYear = this.releaseYear,
        duration = "${this.duration} წ.",
        seasonNum = this.seasonNum,
        genres = this.genres
    )
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