package com.lukakordzaia.core.domain.domainmodels

data class SeasonEpisodesModel(
    val episode: Int,
    val title: String,
    val cover: String?,
    val languages: List<String>,
    val titleDuration: Long? = 0,
    val watchedDuration: Long? = 0
)
