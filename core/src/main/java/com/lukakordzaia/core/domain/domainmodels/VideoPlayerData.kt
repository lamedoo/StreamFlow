package com.lukakordzaia.core.domain.domainmodels

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class VideoPlayerData (
        val titleId: Int,
        val isTvShow: Boolean,
        val chosenSeason: Int,
        val chosenLanguage: String,
        val chosenEpisode: Int,
        val watchedTime: Long,
        val trailerUrl: String?,
        val chosenSubtitle: String? = null,
) : Parcelable
