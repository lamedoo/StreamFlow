package com.lukakordzaia.core.domain.domainmodels

data class SingleTitleModel(
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
    val seasonNum: Int?,
    val country: String?,
    val trailer: String?,
    val watchlist: Boolean?,
    val titleDuration: Long?,
    val watchedDuration: Long?,
    val currentSeason: Int?,
    val currentEpisode: Int?,
    val currentLanguage: String?,
    val visibility: Boolean?,
    val hasMorePage: Boolean? = false
)