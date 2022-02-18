package com.lukakordzaia.core.datamodels

data class NewSeriesModel(
    val id: Int,
    val isTvShow: Boolean,
    val displayName: String?,
    val nameGeo: String?,
    val nameEng: String?,
    val poster: String?,
    val cover: String?,
    val description: String?,
    val imdbId: String?,
    val imdbScore: String?,
    val releaseYear: String?,
    val genres: List<String>?,
    val duration: String?,
    val currentSeason: Int?,
    val currentEpisode: Int?
)