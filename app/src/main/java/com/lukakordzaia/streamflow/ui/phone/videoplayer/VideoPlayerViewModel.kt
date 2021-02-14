package com.lukakordzaia.streamflow.ui.phone.videoplayer

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.exoplayer2.MediaItem
import com.lukakordzaia.streamflow.database.DbDetails
import com.lukakordzaia.streamflow.database.ImoviesDatabase
import com.lukakordzaia.streamflow.datamodels.TitleFiles
import com.lukakordzaia.streamflow.datamodels.VideoPlayerInit
import com.lukakordzaia.streamflow.datamodels.VideoPlayerRelease
import com.lukakordzaia.streamflow.network.Result
import com.lukakordzaia.streamflow.repository.SingleTitleRepository
import com.lukakordzaia.streamflow.ui.baseclasses.BaseViewModel
import kotlinx.coroutines.launch

class VideoPlayerViewModel(private val repository: SingleTitleRepository) : BaseViewModel() {
    val isTvShow = MutableLiveData<Boolean>()
    val numOfSeasons = MutableLiveData<Int>()

    val playBackOptions = MutableLiveData<VideoPlayerInit>()

    private val getSeasonEpisodes: MutableList<String> = ArrayList()
    private val seasonEpisodesIntoUri: MutableList<MediaItem> = ArrayList()

    private val _setSeasonEpisodesUri = MutableLiveData<List<MediaItem>>()
    val setSeasonEpisodesUri: LiveData<List<MediaItem>> = _setSeasonEpisodesUri

    val titleIdForDb = MutableLiveData<Int>()
    private val playbackPositionForDb = MutableLiveData(0L)
    private val titleDurationForDb = MutableLiveData(0L)
    val seasonForDb = MutableLiveData(1)
    private val episodeForDb = MutableLiveData<Int>()
    val languageForDb = MutableLiveData<String>()

    private val getTitleNameList: MutableList<String> = ArrayList()

    private val _setTitleNameList = MutableLiveData<List<String>>()
    val setTitleName: LiveData<List<String>> = _setTitleNameList

    fun initPlayer(isTvShow: Boolean, watchTime: Long, chosenEpisode: Int) {
        if (isTvShow) {
            this.isTvShow.value = isTvShow
        }
        playBackOptions.value = VideoPlayerInit(
            if (isTvShow) chosenEpisode-1 else chosenEpisode,
            watchTime
        )
    }

    fun releasePlayer(videoPlayerRelease: VideoPlayerRelease) {
        playbackPositionForDb.value = videoPlayerRelease.playbackPosition
        episodeForDb.value = videoPlayerRelease.currentWindow
        titleDurationForDb.value = videoPlayerRelease.titleDuration
    }

    fun saveTitleToDb(context: Context, titleId: Int, isTvShow: Boolean, chosenLanguage: String) {
        val database = ImoviesDatabase.getDatabase(context)?.getDao()
        viewModelScope.launch {
            if (playbackPositionForDb.value!! > 0) {
                database?.insertWatchedTitle(
                    DbDetails(
                        titleId,
                        chosenLanguage,
                        playbackPositionForDb.value!!,
                        titleDurationForDb.value!!,
                        isTvShow,
                        seasonForDb.value!!,
                        episodeForDb.value!! + 1
                    )
                )
            }
        }
    }

    private fun clearPlayerForNextSeason() {
        getSeasonEpisodes.clear()
        seasonEpisodesIntoUri.clear()
        getTitleNameList.clear()
    }

    fun getPlaylistFiles(titleId: Int, chosenSeason: Int, chosenLanguage: String) {
        seasonForDb.value = chosenSeason
        titleIdForDb.value = titleId
        languageForDb.value = chosenLanguage
        clearPlayerForNextSeason()
        viewModelScope.launch {
            when (val files = repository.getSingleTitleFiles(titleId, chosenSeason)) {
                is Result.Success -> {
                    val season = files.data.data
                    season.forEach { singleEpisode ->
                        getTitleNameList.add(singleEpisode.title)
                        singleEpisode.files.forEach { singleEpisodeFiles ->
                            checkAvailability(singleEpisodeFiles, chosenLanguage)
                        }
                    }

                    if (getSeasonEpisodes.size < season.size) {
                        newToastMessage("${getSeasonEpisodes.size+1} ეპიზოდიდან ავტომატურად გადაირთვება ინგლისურ ენაზე")
                        val restOfSeason = season.subList(getSeasonEpisodes.size, season.size)
                        restOfSeason.forEach {singleEpisode ->
                            singleEpisode.files.forEach { singleEpisodeFiles ->
                                checkAvailability(singleEpisodeFiles, "ENG")
                            }
                        }
                    }
                    
                    getSeasonEpisodes.forEach {
                        val items = MediaItem.fromUri(Uri.parse(it))
                        seasonEpisodesIntoUri.add(items)
                    }
                    Log.d("episodelinks", getSeasonEpisodes.toString())
                    _setTitleNameList.value = getTitleNameList
                    _setSeasonEpisodesUri.value = seasonEpisodesIntoUri
                }
                is Result.Error -> {
                    Log.d("errornextepisode", files.exception)
                }
            }
        }

    }

    private fun checkAvailability(singleEpisodeFiles: TitleFiles.Data.File, chosenLanguage: String) {
        if (singleEpisodeFiles.lang == chosenLanguage) {
            if (singleEpisodeFiles.files.size == 1) {
                    getSeasonEpisodes.add(singleEpisodeFiles.files[0].src)
            } else if (singleEpisodeFiles.files.size > 1) {
                singleEpisodeFiles.files.forEach {
                    if (it.quality == "HIGH") {
                        getSeasonEpisodes.add(it.src)
                    }
                }
            }
        }
    }

    fun getSingleTitleData(titleId: Int) {
        viewModelScope.launch {
            when (val titleData = repository.getSingleTitleData(titleId)) {
                is Result.Success -> {
                    val data = titleData.data.data

                    if (data.seasons == null) {
                        numOfSeasons.value = 0
                    } else {
                        numOfSeasons.value = data.seasons.data.size
                    }
                }
                is Result.Error -> {
                    Log.d("errorsinglemovies", titleData.exception)
                }
            }
        }
    }
}