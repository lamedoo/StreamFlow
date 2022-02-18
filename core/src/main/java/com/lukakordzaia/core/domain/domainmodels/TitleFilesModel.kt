package com.lukakordzaia.core.domain.domainmodels

data class TitleFilesModel(
    val episode: Int,
    val title: String,
    val cover: String?,
    val languages: List<String>,
    val titleDuration: Long?,
    val watchedDuration: Long?
)
