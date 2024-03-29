package com.lukakordzaia.core.domain.domainmodels

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ContinueWatchingModel(
    val poster: String?,
    val cover: String?,
    val duration: String?,
    val id: Int,
    val isTvShow: Boolean,
    val primaryName: String?,
    val originalName: String?,
    val imdbScore: String?,
    val releaseYear: String?,
    val genres: List<String>?,
    val seasonNum: Int?,
    val watchedDuration: Long,
    val titleDuration: Long,
    val season: Int,
    val episode: Int,
    val language: String
): Parcelable
