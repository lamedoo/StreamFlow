package com.lukakordzaia.streamflow.datamodels

data class AddTitleToFirestore(
    val name: String,
    val isTvShow: Boolean,
    val id: Int,
    val imdbId: String
)
