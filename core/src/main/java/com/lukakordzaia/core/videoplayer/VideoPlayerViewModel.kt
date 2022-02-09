package com.lukakordzaia.core.videoplayer

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lukakordzaia.core.baseclasses.BaseViewModel
import com.lukakordzaia.core.database.continuewatchingdb.ContinueWatchingRoom
import com.lukakordzaia.core.datamodels.EpisodeInfoModel
import com.lukakordzaia.core.datamodels.TitleMediaItemsUri
import com.lukakordzaia.core.datamodels.VideoPlayerData
import com.lukakordzaia.core.network.LoadingState
import com.lukakordzaia.core.network.Result
import com.lukakordzaia.core.network.models.imovies.request.user.PostTitleWatchTimeRequestBody
import com.lukakordzaia.core.network.toEpisodeInfoModel
import com.lukakordzaia.core.utils.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class VideoPlayerViewModel : BaseViewModel() {
    private val _numOfSeasons = MutableLiveData<Int>()
    val numOfSeasons: LiveData<Int> = _numOfSeasons

    private val _availableLanguages = MutableLiveData<List<String>>()
    val availableLanguages: LiveData<List<String>> = _availableLanguages

    private val _availableSubtitles = MutableLiveData<List<String>>()
    val availableSubtitles: LiveData<List<String>> = _availableSubtitles

    private var episodeLink: String? = null
    private var subtitleLink: String? = null
    val mediaAndSubtitle = MutableLiveData<TitleMediaItemsUri>()

    private val _videoPlayerData = MutableLiveData<VideoPlayerData>()
    val videoPlayerData: LiveData<VideoPlayerData> = _videoPlayerData

    private val qualityForDb = MutableLiveData("HIGH")

    private val _setTitleName = MutableLiveData<String>()
    val setTitleName: LiveData<String> = _setTitleName

    var totalEpisodesInSeason = MutableLiveData<Int>()

    val saveLoader = MutableLiveData<LoadingState>()

    private val _newEpisodeStarted = MutableLiveData<Event<Boolean>>()
    val newEpisodeStarted: LiveData<Event<Boolean>> = _newEpisodeStarted

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
                    withContext(Dispatchers.IO) {
                        environment.databaseRepository.insertContinueWatchingInRoom(dbDetails)
                    }
                }
            }
        } else {
            saveLoader.value = LoadingState.ERROR
        }
    }

    fun getTitleFiles(videoPlayerData: VideoPlayerData) {
        viewModelScope.launch {
            when (val files = environment.singleTitleRepository.getSingleTitleFiles(videoPlayerData.titleId, videoPlayerData.chosenSeason)) {
                is Result.Success -> {
                    val data = files.data.data
                    val info = data.toEpisodeInfoModel(if (videoPlayerData.chosenSeason == 0) 0 else videoPlayerData.chosenEpisode - 1, videoPlayerData.chosenLanguage)

                    _setTitleName.value = info.episodeName
                    _availableLanguages.value = info.availableLanguages
                    _availableSubtitles.value = info.availableSubs

                    totalEpisodesInSeason.value = files.data.data.size

                    checkAudio(info, videoPlayerData.chosenLanguage, videoPlayerData.chosenSubtitle)

                    val mediaItems = TitleMediaItemsUri(episodeLink, subtitleLink)
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

    private fun checkAudio(data: EpisodeInfoModel, chosenLanguage: String, chosenSubtitle: String?) {
        episodeLink = null

        data.episodeFiles?.forEach {
            if (chosenLanguage.equals(it.fileLanguage, true)) {
                episodeLink = it.fileUrl
            }
        }

        // If new episode is not available in same language, automatically switches to first available language
        if (episodeLink.isNullOrEmpty()) {
            newToastMessage("$chosenLanguage - ვერ მოიძებნა. ავტომატურად ჩაირთო - ${data.episodeFiles!![0].fileLanguage}")
            episodeLink = data.episodeFiles[0].fileUrl
            checkSubtitles(data.episodeSubs, data.episodeFiles[0].fileLanguage, chosenSubtitle)
        } else {
            checkSubtitles(data.episodeSubs, chosenLanguage, chosenSubtitle)
        }
    }

    private fun checkSubtitles(fileSubtitles: List<EpisodeInfoModel.EpisodeSubtitles>?, chosenLanguage: String, chosenSubtitle: String?) {
        subtitleLink = null

        if (!fileSubtitles.isNullOrEmpty()) {
            if (chosenSubtitle == null || chosenSubtitle == "გათიშვა") {
                when (fileSubtitles.size) {
                    1 -> {
                        subtitleLink = fileSubtitles[0].subUrl
                        _videoPlayerData.value = _videoPlayerData.value?.copy(chosenSubtitle = fileSubtitles[0].subLanguage)
                    }
                    else -> {
                        fileSubtitles.forEach {
                            if (it.subLanguage.equals(chosenLanguage, true)) {
                                subtitleLink = it.subUrl
                                _videoPlayerData.value = _videoPlayerData.value?.copy(chosenSubtitle = it.subLanguage)
                            }
                        }
                    }
                }
            } else {
                fileSubtitles.forEach {
                    if (it.subLanguage.equals(chosenSubtitle, true)) {
                        subtitleLink = it.subUrl
                    }
                }

                // If chosen subtitle language is not available for next episode, automatically switches to first available subtitle
                if (subtitleLink == null) {
                    subtitleLink = fileSubtitles[0].subUrl
                    _videoPlayerData.value = _videoPlayerData.value?.copy(chosenSubtitle = fileSubtitles[0].subLanguage)
                    newToastMessage("$chosenSubtitle - სუბტიტრები ვერ მოიძებნა. ავტომატურად ჩაირთო - ${fileSubtitles[0].subLanguage} სუბტიტრები")
                }
            }
        } else {
            subtitleLink = null
            _videoPlayerData.value = _videoPlayerData.value?.copy(chosenSubtitle = "გათიშვა")
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

    fun episodeHasStarted() {
        _newEpisodeStarted.value = Event(true)
    }
}