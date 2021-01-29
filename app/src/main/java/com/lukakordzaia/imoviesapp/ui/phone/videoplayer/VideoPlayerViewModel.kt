package com.lukakordzaia.imoviesapp.ui.phone.videoplayer

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.PlayerView
import com.lukakordzaia.imoviesapp.database.ImoviesDatabase
import com.lukakordzaia.imoviesapp.database.WatchedDetails
import com.lukakordzaia.imoviesapp.datamodels.TitleFiles
import com.lukakordzaia.imoviesapp.datamodels.VideoPlayerOptions
import com.lukakordzaia.imoviesapp.helpers.MediaPlayerClass
import com.lukakordzaia.imoviesapp.network.Result
import com.lukakordzaia.imoviesapp.repository.TitleFilesRepository
import com.lukakordzaia.imoviesapp.ui.baseclasses.BaseViewModel
import kotlinx.coroutines.launch

class VideoPlayerViewModel : BaseViewModel() {
    private val repository = TitleFilesRepository()
    private val mediaPlayer = MediaPlayerClass()

    private var playWhenReady = MutableLiveData(true)
    private var currentWindow = MutableLiveData<Int>(0)
    private var playbackPosition = MutableLiveData<Long>(0)

    private val episodesUri = MutableLiveData<MutableList<MediaItem>>()

    private val seasonEpisodes: MutableList<String> = ArrayList()
    private val seasonEpisodesUri: MutableList<MediaItem> = ArrayList()

    private val seasonForDb = MutableLiveData(1)
    private val episodeForDb = MutableLiveData<Int>()
    private val titleIdForDb = MutableLiveData<Int>()
    private val languageForDb = MutableLiveData<String>()

    private val getTitleNameList: MutableList<String> = mutableListOf()

    private val setTitleNameList = MutableLiveData<List<String>>()
    val setTitleName: LiveData<List<String>> = setTitleNameList

    fun initPlayer(
        context: Context,
        playerView: PlayerView,
        isTvShow: Boolean,
        watchTime: Long,
        chosenEpisode: Int,
        trailerUrl: String?
    ) {
        if (watchTime > 0L) {
            playbackPosition.value = watchTime
        }
        if (isTvShow) {
            currentWindow.value = chosenEpisode - 1
        }
        val playBackOptions = VideoPlayerOptions(
                playWhenReady.value!!,
                currentWindow.value!!,
                playbackPosition.value!!,
                trailerUrl
        )
        mediaPlayer.initPlayer(context, playerView, playBackOptions)
    }

    fun releasePlayer() {
        mediaPlayer.releasePlayer {
            playWhenReady.value = it.playWhenReady
            playbackPosition.value = it.playbackPosition
            currentWindow.value = it.currentWindow
            episodeForDb.value = it.currentWindow
        }
    }

    fun initHeader(header: TextView) {
        mediaPlayer.initHeader(header)
    }

    fun addEpisodeNames(episodeNames: List<String>) {
        mediaPlayer.addEpisodeNames(episodeNames)
    }

    fun saveTitleToDb(context: Context, titleId: Int, isTvShow: Boolean, chosenLanguage: String) {
        val database = ImoviesDatabase.getDatabase(context)?.getDao()
        viewModelScope.launch {
            if (playbackPosition.value!! > 0) {
                database?.insertWatchedTitle(WatchedDetails(
                        titleId,
                        chosenLanguage,
                        playbackPosition.value!!,
                        isTvShow,
                        seasonForDb.value!!,
                        episodeForDb.value!! + 1
                )
                )
            }
        }
    }

    fun getNextSeasonEpisodes() {
        seasonEpisodes.clear()
        if (!seasonEpisodesUri.isNullOrEmpty()) {
            seasonEpisodesUri.clear()
            viewModelScope.launch {
                when (val files = repository.getSingleTitleFiles(titleIdForDb.value!!, seasonForDb.value!! + 1))  {
                    is Result.Success -> {
                        seasonForDb.value = seasonForDb.value!! + 1
                        val season = files.data.data
                        season.forEach { singleEpisode ->
                            singleEpisode.files!!.forEach { singleFiles ->
                                checkAvailability(singleFiles, languageForDb.value!!)
                            }
                        }
                        seasonEpisodes.forEach {
                            val item = MediaItem.fromUri(Uri.parse(it))
                            seasonEpisodesUri.add(item)
                        }
                        episodesUri.value = seasonEpisodesUri
                        mediaPlayer.addAllEpisodes(episodesUri.value!!)
                    }
                }
            }
        }
    }

    fun getPlaylistFiles(titleId: Int, chosenSeason: Int, chosenLanguage: String) {
        seasonForDb.value = chosenSeason
        titleIdForDb.value = titleId
        languageForDb.value = chosenLanguage
        if (episodesUri.value == null) {
            viewModelScope.launch {
                when (val files = repository.getSingleTitleFiles(titleId, chosenSeason)) {
                    is Result.Success -> {
                        val season = files.data.data
                        season.forEach { singleEpisode ->
                            getTitleNameList.add(singleEpisode.title)
                            singleEpisode.files!!.forEach { singleFiles ->
                                checkAvailability(singleFiles, chosenLanguage)
                            }
                        }
                        seasonEpisodes.forEach {
                            val items = MediaItem.fromUri(Uri.parse(it))
                            seasonEpisodesUri.add(items)
                        }
                        setTitleNameList.value = getTitleNameList
                        episodesUri.value = seasonEpisodesUri
                        mediaPlayer.addAllEpisodes(episodesUri.value!!)
                        Log.d("episodesaaa", "${seasonEpisodes}")
                    }
                    is Result.Error -> {
                        Log.d("errornextepisode", files.exception)
                    }
                }
            }
        }
    }

    private fun checkAvailability(singleFiles: TitleFiles.Data.File, chosenLanguage: String) {
        if (singleFiles.lang == chosenLanguage) {
            if (singleFiles.files.size == 1) {
                if (singleFiles.files[0].quality == "MEDIUM") {
                    seasonEpisodes.add(singleFiles.files[0].src!!)
                } else {
                    seasonEpisodes.add(singleFiles.files[0].src!!)
                }
            } else if (singleFiles.files.size > 1) {
                singleFiles.files.forEach {
                    if (it.quality == "HIGH" && it.src != null) {
                        seasonEpisodes.add(it.src)
                    }
                }
            }
        }
    }
}