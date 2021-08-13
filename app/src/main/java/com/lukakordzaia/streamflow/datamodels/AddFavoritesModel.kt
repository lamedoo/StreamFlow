package com.lukakordzaia.streamflow.datamodels

data class AddFavoritesModel(
    val name: String,
    val isTvShow: Boolean,
    val id: Int,
    val imdbId: String
)
