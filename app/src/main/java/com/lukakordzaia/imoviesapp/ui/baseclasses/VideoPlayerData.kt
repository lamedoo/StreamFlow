package com.lukakordzaia.imoviesapp.ui.baseclasses

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class VideoPlayerData (
        val titleId: Int,
        val isTvShow: Boolean,
        val chosenSeason: Int,
        val chosenLanguage: String,
        val chosenEpisode: Int,
        val watchedTime: Long
) : Parcelable
