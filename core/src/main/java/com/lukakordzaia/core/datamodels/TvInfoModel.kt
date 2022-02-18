package com.lukakordzaia.core.datamodels

data class TvInfoModel(
    val id: Int,
    val isTvShow: Boolean,
    val displayName: String?,
    val nameGeo: String?,
    val nameEng: String?,
    val cover: String?,
    val imdbScore: String?,
    val releaseYear: String?,
    val duration: String?,
    val seasonNum: Int?,
    val genres: List<String>?
)