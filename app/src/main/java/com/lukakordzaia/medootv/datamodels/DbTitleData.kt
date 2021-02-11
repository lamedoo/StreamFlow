package com.lukakordzaia.medootv.datamodels

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DbTitleData(
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
