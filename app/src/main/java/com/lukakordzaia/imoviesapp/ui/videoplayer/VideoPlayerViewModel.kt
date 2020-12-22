package com.lukakordzaia.imoviesapp.ui.videoplayer

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.PlayerView
import com.lukakordzaia.imoviesapp.database.ImoviesDatabase
import com.lukakordzaia.imoviesapp.database.WatchedDetails
import com.lukakordzaia.imoviesapp.network.Result
import com.lukakordzaia.imoviesapp.network.datamodels.TitleFiles
import com.lukakordzaia.imoviesapp.network.datamodels.VideoPlayerOptions
import com.lukakordzaia.imoviesapp.repository.TitleFilesRepository
import com.lukakordzaia.imoviesapp.ui.baseclasses.BaseViewModel
import kotlinx.coroutines.launch

class VideoPlayerViewModel : BaseViewModel() {
    private val repository = TitleFilesRepository()
    private val mediaPlayer = MediaPlayerClass()

    private var playWhenReady = MutableLiveData(true)
    private var currentWindow = MutableLiveData<Int>(0)
    private var playbackPosition = MutableLiveData<Long>(0)
    private var mediaLink = MutableLiveData<String>()

    private val episodesUri = MutableLiveData<MutableList<MediaItem>>()

    private val seasonEpisodes: MutableList<String> = ArrayList()
    private val seasonEpisodesUri: MutableList<MediaItem> = ArrayList()

    private val seasonForDb = MutableLiveData(1)
    private val episodeForDb = MutableLiveData<Int>()

    fun initPlayer(
        context: Context,
        playerView: PlayerView,
        media: String,
        isTvShow: Boolean,
        watchTime: Long,
        chosenEpisode: Int
    ) {
        mediaLink.value = media
        currentWindow.value = chosenEpisode - 1
        if (watchTime > 0L) {
            playbackPosition.value = watchTime
        }
        if (isTvShow) {
            val playBackOptions = VideoPlayerOptions(
                    playWhenReady.value!!,
                    currentWindow.value!!,
                    playbackPosition.value!!,
                    null
            )
            mediaPlayer.initPlayer(context, playerView, playBackOptions)
        } else {
            val playBackOptions = VideoPlayerOptions(
                    playWhenReady.value!!,
                    0,
                    playbackPosition.value!!,
                    mediaLink.value
            )
            mediaPlayer.initPlayer(context, playerView, playBackOptions)
            Log.d("medialink1", mediaLink.value.toString())
        }
    }

    fun releasePlayer() {
        mediaPlayer.releasePlayer {
            playWhenReady.value = it.playWhenReady
            playbackPosition.value = it.playbackPosition
            currentWindow.value = it.currentWindow
            episodeForDb.value = it.currentWindow
        }
    }

    fun saveTitleToDb(context: Context, titleId: Int, movieLink: String, isTvShow: Boolean) {
        val database = ImoviesDatabase.getDatabase(context)?.getDao()
        viewModelScope.launch {
            if (playbackPosition.value!! > 0 && !isTvShow) {
                database?.insertWatchedTitle(
                    WatchedDetails(
                        titleId,
                        playbackPosition.value!!,
                        false,
                        movieLink,
                        0,
                        0
                    )
                )
            } else if (playbackPosition.value!! > 0 && isTvShow) {
                database?.insertWatchedTitle(
                    WatchedDetails(
                        titleId,
                        playbackPosition.value!!,
                        true,
                        "",
                        seasonForDb.value!!,
                        episodeForDb.value!! + 1
                    )
                )
            }
        }
    }

    fun getPlaylistFiles(titleId: Int, chosenSeason: Int, chosenLanguage: String) {
        if (chosenSeason != 0) {
            seasonForDb.value = chosenSeason
            if (episodesUri.value == null) {
                viewModelScope.launch {
                    when (val files = repository.getSingleTitleFiles(titleId, chosenSeason)) {
                        is Result.Success -> {
                            val season = files.data.data
                            season.forEach { singleEpisode ->
                                singleEpisode.files!!.forEach { singleFiles ->
                                    checkAvailability(singleFiles, chosenLanguage)
                                }
                            }
                            seasonEpisodes.forEach {
                                val items = MediaItem.fromUri(Uri.parse(it))
                                seasonEpisodesUri.add(items)
                            }
                            episodesUri.value = seasonEpisodesUri
                            mediaPlayer.addAllEpisodes(episodesUri.value!!)
                            Log.d("episodes", "${seasonEpisodes}")
                        }
                        is Result.Error -> {
                            Log.d("errornextepisode", files.exception)
                        }
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