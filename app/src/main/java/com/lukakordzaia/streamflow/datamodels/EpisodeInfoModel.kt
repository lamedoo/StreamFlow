package com.lukakordzaia.streamflow.datamodels

data class EpisodeInfoModel(
    val totalEpisodes: Int,
    val episodeName: String,
    val availableLanguages: List<String>,
    val availableSubs: List<String>,
    val episodeFiles: List<EpisodeFiles>?,
    val episodeSubs: List<EpisodeSubtitles>?
) {
    data class EpisodeFiles(
        val fileLanguage: String,
        val fileUrl: String?
    )

    data class EpisodeSubtitles(
        val subLanguage: String,
        val subUrl: String?
    )
}