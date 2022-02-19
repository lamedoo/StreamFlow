package com.lukakordzaia.core.network.models.imovies.request.user

data class PostTitleWatchTimeRequestFull(
    val id: Int,
    val season: Int,
    val episode: Int,
    val titleWatchTime: PostTitleWatchTimeRequestBody
)
