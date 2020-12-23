package com.lukakordzaia.imoviesapp.network.datamodels

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class WatchedTitleData(
    val cover: String?,
    val duration: Int?,
    val id: Int,
    val isTvShow: Boolean,
    val primaryName: String?,
    val originalName: String?,
    val watchedTime: Long,
    val season: Int,
    val episode: Int,
    val language: String
): Parcelable
