package com.lukakordzaia.streamflow.datamodels

data class VideoPlayerInfo(
        val titleId: Int,
        val isTvShow: Boolean,
        val chosenSeason: Int,
        val chosenEpisode: Int,
        val chosenLanguage: String
)
