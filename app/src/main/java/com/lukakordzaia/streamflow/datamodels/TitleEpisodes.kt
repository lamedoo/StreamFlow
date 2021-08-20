package com.lukakordzaia.streamflow.datamodels

data class TitleEpisodes(
        val episodeNum: Int,
        val episodeName: String,
        val episodePoster: String,
        val titleDuration: Long? = 0,
        val watchDuration: Long? = 0,
)
