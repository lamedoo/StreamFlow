package com.lukakordzaia.streamflow.ui.shared

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lukakordzaia.streamflow.database.continuewatchingdb.ContinueWatchingRoom
import com.lukakordzaia.streamflow.datamodels.TitleMediaItemsUri
import com.lukakordzaia.streamflow.datamodels.VideoPlayerData
import com.lukakordzaia.streamflow.network.LoadingState
import com.lukakordzaia.streamflow.network.Result
import com.lukakordzaia.streamflow.network.models.imovies.request.user.PostTitleWatchTimeRequestBody
import com.lukakordzaia.streamflow.network.models.imovies.response.singletitle.GetSingleTitleFilesResponse
import com.lukakordzaia.streamflow.ui.baseclasses.BaseViewModel
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class VideoPlayerViewModel : BaseViewModel() {
    private val _numOfSeasons = MutableLiveData<Int>()
    val numOfSeasons: LiveData<Int> = _numOfSeasons

    private var getEpisode: String = ""
    private var getSubtitles: Uri? = null
    val mediaAndSubtitle = MutableLiveData<TitleMediaItemsUri>()

    private val _videoPlayerData = MutableLiveData<VideoPlayerData>()
    val videoPlayerData: LiveData<VideoPlayerData> = _videoPlayerData

    private val qualityForDb = MutableLiveData("HIGH")

    private val _setTitleName = MutableLiveData<String>()
    val setTitleName: LiveData<String> = _setTitleName

    var totalEpisodesInSeason = MutableLiveData<Int>()

    val saveLoader = MutableLiveData<LoadingState>()

    fun setVideoPlayerData(videoPlayerData: VideoPlayerData) {
        _videoPlayerData.value = videoPlayerData

        if (videoPlayerData.trailerUrl == null) {
            getTitleFiles(videoPlayerData)
            getSingleTitleData(videoPlayerData.titleId)
        }
    }

    fun addContinueWatching(playBackDuration: Long, titleDuration: Long) {
        val dbDetails = ContinueWatchingRoom(
            videoPlayerData.value!!.titleId,
            videoPlayerData.value!!.chosenLanguage,
            TimeUnit.MILLISECONDS.toSeconds(playBackDuration),
            TimeUnit.MILLISECONDS.toSeconds(titleDuration),
            videoPlayerData.value!!.isTvShow,
            videoPlayerData.value!!.chosenSeason,
            videoPlayerData.value!!.chosenEpisode
        )
        if (playBackDuration > 0 && titleDuration > 0) {
            if (sharedPreferences.getLoginToken() != "") {
                saveLoader.value = LoadingState.LOADING
                viewModelScope.launch {
                    when (environment.userRepository.titleWatchTime(
                        PostTitleWatchTimeRequestBody(
                            duration = TimeUnit.MILLISECONDS.toSeconds(titleDuration).toInt(),
                            progress = TimeUnit.MILLISECONDS.toSeconds(playBackDuration).toInt(),
                            language = videoPlayerData.value!!.chosenLanguage,
                            quality = qualityForDb.value!!
                        ),
                        videoPlayerData.value!!.titleId,
                        videoPlayerData.value!!.chosenSeason,
                        videoPlayerData.value!!.chosenEpisode
                    )) {
                        is Result.Success -> {
                            saveLoader.value = LoadingState.LOADED
                        }
                        is Result.Error -> {
                            saveLoader.value = LoadingState.ERROR
                        }
                    }
                }
            } else {
                viewModelScope.launch {
                    environment.databaseRepository.insertContinueWatchingInRoom(dbDetails)
                }
            }
        } else {
            saveLoader.value = LoadingState.ERROR
        }
    }

    fun getTitleFiles(videoPlayerData: VideoPlayerData) {
        setNoInternet(false)
        getEpisode = ""

        viewModelScope.launch {
            when (val files = environment.singleTitleRepository.getSingleTitleFiles(videoPlayerData.titleId, videoPlayerData.chosenSeason)) {
                is Result.Success -> {
                    totalEpisodesInSeason.value = files.data.data.size

                    val season = if (videoPlayerData.chosenSeason == 0) {
                        files.data.data[0]
                    } else {
                        files.data.data[videoPlayerData.chosenEpisode - 1]
                    }

                    _setTitleName.value = season.title

                    season.files.forEach { singleFiles ->
                        checkAvailability(singleFiles, videoPlayerData.chosenLanguage)
                    }

                    val episodeIntoUri = if (getEpisode.isNotBlank()) {
                        Uri.parse(getEpisode)
                    } else {
                        checkAvailability(season.files[0], season.files[0].lang)
                        newToastMessage("${videoPlayerData.chosenLanguage} - ვერ მოიძებნა. ავტომატურად ჩაირთო - ${season.files[0].lang}")
                        Uri.parse(getEpisode)
                    }

                    val mediaItems = TitleMediaItemsUri(episodeIntoUri, getSubtitles)
                    mediaAndSubtitle.value = mediaItems
                }
                is Result.Error -> {
                    Log.d("errornextepisode", files.exception)
                }
                is Result.Internet -> {
                    setNoInternet()
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
                    getSubtitles = Uri.parse(singleEpisodeFilesGetSingle.subtitles[0]!!.url)
                } else if (singleEpisodeFilesGetSingle.subtitles.size > 1) {
                    singleEpisodeFilesGetSingle.subtitles.forEach {
                        if (it!!.lang.equals(chosenLanguage, true)) {
                            getSubtitles = Uri.parse(it.url)
                        }
                    }
                }
            } else {
                getSubtitles = null
            }
        }
    }

    fun getSingleTitleData(titleId: Int) {
        viewModelScope.launch {
            when (val titleData = environment.singleTitleRepository.getSingleTitleData(titleId)) {
                is Result.Success -> {
                    val data = titleData.data.data

                    if (!data.isTvShow || data.seasons == null) {
                        _numOfSeasons.value = 0
                    } else {
                        _numOfSeasons.value = data.seasons.data.size
                    }
                }
                is Result.Error -> {
                    Log.d("errorsinglemovies", titleData.exception)
                }
            }
        }
    }
}