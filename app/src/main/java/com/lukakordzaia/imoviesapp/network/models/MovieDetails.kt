package com.lukakordzaia.imoviesapp.network.models

import com.google.gson.annotations.SerializedName

data class MovieDetails(
        @SerializedName("numOfSeasons")
        val numOfSeasons: Int,
        @SerializedName("isTvShow")
        val isTvShow: Boolean,
)
