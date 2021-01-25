package com.lukakordzaia.imoviesapp.datamodels

import com.google.gson.annotations.SerializedName

data class TitleDetails(
        @SerializedName("numOfSeasons")
        val numOfSeasons: Int,
        @SerializedName("isTvShow")
        val isTvShow: Boolean,
)
