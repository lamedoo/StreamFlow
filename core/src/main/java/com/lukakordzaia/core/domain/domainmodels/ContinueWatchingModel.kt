package com.lukakordzaia.core.domain.domainmodels

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ContinueWatchingModel(
    val cover: String?,
    val duration: Int?,
    val id: Int,
    val isTvShow: Boolean,
    val primaryName: String?,
    val originalName: String?,
    val watchedDuration: Long,
    val titleDuration: Long,
    val season: Int,
    val episode: Int,
    val language: String
): Parcelable
