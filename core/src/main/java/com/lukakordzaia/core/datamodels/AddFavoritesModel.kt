package com.lukakordzaia.core.datamodels

data class AddFavoritesModel(
    val name: String,
    val isTvShow: Boolean,
    val id: Int,
    val imdbId: String
)
