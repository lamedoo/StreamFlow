package com.lukakordzaia.imoviesapp.ui.videoplayer

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.PlayerView
import com.lukakordzaia.imoviesapp.network.Result
import com.lukakordzaia.imoviesapp.network.models.TitleFiles
import com.lukakordzaia.imoviesapp.network.models.VideoPlayerOptions
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

    fun initPlayer(context: Context, playerView: PlayerView, media: String, isTvShow: Boolean) {
        mediaLink.value = media
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
                    currentWindow.value!!,
                    playbackPosition.value!!,
                    mediaLink.value
            )
            mediaPlayer.initPlayer(context, playerView, playBackOptions)
        }
    }

    fun releasePlayer() {
        mediaPlayer.releasePlayer {
            playWhenReady.value = it.playWhenReady
            playbackPosition.value = it.playbackPosition
            currentWindow.value = it.currentWindow
        }
    }

    fun getPlaylistFiles(movieId: Int, chosenSeason: Int, chosenEpisode: Int, chosenLanguage: String) {
        if (chosenSeason != 0) {
            viewModelScope.launch {
                when (val files = repository.getSingleTitleFiles(movieId, chosenSeason)) {
                    is Result.Success -> {
                        val season = files.data.data
                        val allEpisodes = season.subList(chosenEpisode - 1, season.size)
                        allEpisodes.forEach { singleEpisode ->
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
//        seasonEpisodes.forEach {
//            val items = MediaItem.fromUri(Uri.parse(it))
//            seasonEpisodesUri.add(items)
//        }
//        episodesUri.value = seasonEpisodesUri
//        mediaPlayer.addAllEpisodes(episodesUri.value!!)
//        Log.d("episodes", "${seasonEpisodes}")
    }
}