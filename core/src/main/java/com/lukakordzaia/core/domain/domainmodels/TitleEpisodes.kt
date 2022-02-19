package com.lukakordzaia.core.domain.domainmodels

data class TitleEpisodes(
    val titleId: Int,
    val episodeNum: Int,
    val episodeName: String,
    val episodePoster: String,
    val languages: List<String>,
    val titleDuration: Long? = 0,
    val watchDuration: Long? = 0,
)
