package com.lukakordzaia.streamflow.helpers

import com.lukakordzaia.streamflow.datamodels.SingleTitleModel
import com.lukakordzaia.streamflow.network.models.imovies.response.singletitle.GetSingleTitleResponse
import com.lukakordzaia.streamflow.network.models.imovies.response.titles.GetTitlesResponse

class MapTitleData {
    fun list(titles: List<GetTitlesResponse.Data>): List<SingleTitleModel> {
        val list: MutableList<SingleTitleModel> = ArrayList()

        titles.forEach {
            list.add(
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
                    trailer = if (it.trailers?.data?.isNotEmpty() == true) it.trailers.data[0]?.fileUrl else null
                )
            )
        }

        return list
    }

    fun single(title: GetSingleTitleResponse.Data): SingleTitleModel {
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
            trailer = if (title.trailers.data.isNotEmpty()) title.trailers.data[0].fileUrl else null
        )
    }
}