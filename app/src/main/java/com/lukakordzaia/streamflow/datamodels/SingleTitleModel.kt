package com.lukakordzaia.streamflow.datamodels

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
    val duration: String?,
    val seasonNum: Int?,
    val country: String?,
    val trailer: String?,
    val watchlist: Boolean?
)