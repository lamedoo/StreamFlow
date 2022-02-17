package com.lukakordzaia.core.domain.domainmodels

data class AddFavoritesModel(
    val name: String,
    val isTvShow: Boolean,
    val id: Int,
    val imdbId: String
)
