package com.lukakordzaia.streamflow.ui.shared

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.exoplayer2.MediaItem
import com.lukakordzaia.streamflow.database.continuewatchingdb.ContinueWatchingRoom
import com.lukakordzaia.streamflow.datamodels.PlayerDurationInfo
import com.lukakordzaia.streamflow.datamodels.TitleMediaItemsUri
import com.lukakordzaia.streamflow.datamodels.VideoPlayerInfo
import com.lukakordzaia.streamflow.network.LoadingState
import com.lukakordzaia.streamflow.network.Result
import com.lukakordzaia.streamflow.network.models.imovies.request.user.PostTitleWatchTimeRequestBody
import com.lukakordzaia.streamflow.network.models.imovies.response.singletitle.GetSingleTitleFilesResponse
import com.lukakordzaia.streamflow.ui.baseclasses.BaseViewModel
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class VideoPlayerViewModel : BaseViewModel() {
    private val numOfSeasons = MutableLiveData<Int>()

    private var getEpisode: String = ""
    private var getSubtitles: String = ""
    val mediaAndSubtitle = MutableLiveData<TitleMediaItemsUri>()

    private val _videoPlayerInfo = MutableLiveData<VideoPlayerInfo>()
    val videoPlayerInfo: LiveData<VideoPlayerInfo> = _videoPlayerInfo

    private val playbackPositionForDb = MutableLiveData(0L)
    private val titleDurationForDb = MutableLiveData(0L)
    private val qualityForDb = MutableLiveData("HIGH")

    private val _setTitleNameList = MutableLiveData<String>()
    val setTitleName: LiveData<String> = _setTitleNameList

    var totalEpisodesInSeason = MutableLiveData<Int>()

    val saveLoader = MutableLiveData<LoadingState>()

    fun setVideoPlayerInfo(playerDurationInfo: PlayerDurationInfo) {
        playbackPositionForDb.value = playerDurationInfo.playbackPosition
        titleDurationForDb.value = playerDurationInfo.titleDuration
    }

    fun addContinueWatching() {
        val dbDetails = ContinueWatchingRoom(
                videoPlayerInfo.value!!.titleId,
                videoPlayerInfo.value!!.chosenLanguage,
                playbackPositionForDb.value!!,
                titleDurationForDb.value!!,
                videoPlayerInfo.value!!.isTvShow,
                videoPlayerInfo.value!!.chosenSeason,
                videoPlayerInfo.value!!.chosenEpisode
        )
        if (playbackPositionForDb.value!! > 0 && titleDurationForDb.value!! > 0) {
            if (authSharedPreferences.getLoginToken() != "") {
                saveLoader.value = LoadingState.LOADING
                viewModelScope.launch {
                    when (val time = environment.userRepository.titleWatchTime(
                        PostTitleWatchTimeRequestBody(
                            duration = TimeUnit.MILLISECONDS.toSeconds(titleDurationForDb.value!!).toInt(),
                            progress = TimeUnit.MILLISECONDS.toSeconds(playbackPositionForDb.value!!).toInt(),
                            language = videoPlayerInfo.value!!.chosenLanguage,
                            quality = qualityForDb.value!!
                        ),
                        videoPlayerInfo.value!!.titleId,
                        videoPlayerInfo.value!!.chosenSeason,
                        videoPlayerInfo.value!!.chosenEpisode
                    )) {
                        is Result.Success -> {
                            saveLoader.value = LoadingState.LOADED
                        }
                        is Result.Error -> {
                            saveLoader.value = LoadingState.LOADING
                        }
                    }
                }
            } else {
                viewModelScope.launch {
                    environment.databaseRepository.insertContinueWatchingInRoom(dbDetails)
                }
            }
        }
    }

    fun getTitleFiles(videoPlayerInfo: VideoPlayerInfo) {
        _videoPlayerInfo.value = VideoPlayerInfo(videoPlayerInfo.titleId,
                videoPlayerInfo.isTvShow,
                videoPlayerInfo.chosenSeason,
                videoPlayerInfo.chosenEpisode,
                videoPlayerInfo.chosenLanguage
        )

        getEpisode = ""

        viewModelScope.launch {
            when (val files = environment.singleTitleRepository.getSingleTitleFiles(videoPlayerInfo.titleId, videoPlayerInfo.chosenSeason)) {
                is Result.Success -> {
                    totalEpisodesInSeason.value = files.data.data.size

                    val season = if (videoPlayerInfo.chosenSeason == 0) {
                        files.data.data[0]
                    } else {
                        files.data.data[videoPlayerInfo.chosenEpisode - 1]
                    }

                    _setTitleNameList.value = season.title

                    season.files.forEach { singleFiles ->
                        checkAvailability(singleFiles, videoPlayerInfo.chosenLanguage)
                    }

                    val episodeIntoUri = if (getEpisode.isNotBlank()) {
                        MediaItem.fromUri(Uri.parse(getEpisode))
                    } else {
                        checkAvailability(season.files[0], season.files[0].lang)
                        newToastMessage("${videoPlayerInfo.chosenLanguage} - ვერ მოიძებნა. ავტომატურად ჩაირთო - ${season.files[0].lang}")
                        MediaItem.fromUri(Uri.parse(getEpisode))
                    }

                    val mediaItems = TitleMediaItemsUri(episodeIntoUri, getSubtitles)
                    mediaAndSubtitle.value = mediaItems
                }
                is Result.Error -> {
                    Log.d("errornextepisode", files.exception)
                }
            }
        }
    }

    private fun checkAvailability(singleEpisodeFilesGetSingle: GetSingleTitleFilesResponse.Data.File, chosenLanguage: String) {
        if (singleEpisodeFilesGetSingle.lang == chosenLanguage) {
            if (singleEpisodeFilesGetSingle.files.size == 1) {
                getEpisode = singleEpisodeFilesGetSingle.files[0].src
            } else if (singleEpisodeFilesGetSingle.files.size > 1) {
                singleEpisodeFilesGetSingle.files.forEach {
                    if (it.quality == "HIGH") {
                        getEpisode = it.src
                    }
                }
            }

            if (!singleEpisodeFilesGetSingle.subtitles.isNullOrEmpty()) {
                if (singleEpisodeFilesGetSingle.subtitles.size == 1) {
                    getSubtitles = singleEpisodeFilesGetSingle.subtitles[0]!!.url
                } else if (singleEpisodeFilesGetSingle.subtitles.size > 1) {
                    singleEpisodeFilesGetSingle.subtitles.forEach {
                        if (it!!.lang.equals(chosenLanguage, true)) {
                            getSubtitles = it.url
                        }
                    }
                }
            } else {
                getSubtitles = "0"
            }
        }
    }

    fun getSingleTitleData(titleId: Int) {
        viewModelScope.launch {
            when (val titleData = environment.singleTitleRepository.getSingleTitleData(titleId)) {
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