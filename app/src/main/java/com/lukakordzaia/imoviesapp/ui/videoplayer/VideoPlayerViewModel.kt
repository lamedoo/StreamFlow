package com.lukakordzaia.imoviesapp.ui.videoplayer

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import com.lukakordzaia.imoviesapp.network.Result
import com.lukakordzaia.imoviesapp.repository.MovieFilesRepository
import com.lukakordzaia.imoviesapp.ui.baseclasses.BaseViewModel
import kotlinx.coroutines.launch

class VideoPlayerViewModel : BaseViewModel() {
    private val repository = MovieFilesRepository()
    private var playWhenReady = MutableLiveData(true)
    private var currentWindow = MutableLiveData(0)
    private var playbackPosition = MutableLiveData<Long>(0)
    private var player = MutableLiveData<SimpleExoPlayer>()
    private var mediaLink = MutableLiveData<String>()

    private val episodes = MutableLiveData<MutableList<MediaItem>>()

    fun initPlayer(context: Context, playerView: PlayerView, media: String, isTvShow: Boolean){
        if (isTvShow) {
            player.value = SimpleExoPlayer.Builder(context).build()
            playerView.player = player.value
            player.value?.playWhenReady = playWhenReady.value == true
            player.value?.seekTo(playbackPosition.value!!)
            player.value?.prepare()
            player.value?.play()
        } else {
            mediaLink.value = media
            val firsItem = MediaItem.fromUri(Uri.parse(mediaLink.value))

            player.value = SimpleExoPlayer.Builder(context).build()
            playerView.player = player.value
            player.value?.playWhenReady = playWhenReady.value == true
            player.value?.setMediaItem(firsItem)
            player.value?.seekTo(playbackPosition.value!!)
            player.value?.prepare()
            player.value?.play()
        }

    }

    fun releasePlayer() {
        if (player.value == null) {
            return
        }
        playWhenReady.value = player.value!!.playWhenReady
        playbackPosition.value = player.value!!.currentPosition
        currentWindow.value = player.value!!.currentWindowIndex
        player.value!!.release()
        player.value = null
    }

    fun getPlaylistFiles(movieId: Int, chosenSeason: Int, chosenEpisode: Int, chosenLanguage: String) {
        val seasonEpisodes: MutableList<String> = ArrayList()
        val seasonEpisodesUri: MutableList<MediaItem> = ArrayList()
        if (chosenSeason != 0) {
            viewModelScope.launch {
                when (val files = repository.getSingleMovieFiles(movieId, chosenSeason)) {
                    is Result.Success -> {
                        val season = files.data.data.subList(chosenEpisode - 1, files.data.data.size)
                        season.forEach { singleSeason ->
                            singleSeason.files!!.forEach { singleFiles ->
                                if (singleFiles.lang == chosenLanguage) {
                                    singleFiles.files!!.forEach {
                                        if (it.quality == "HIGH") {
                                            seasonEpisodes.add(it.src!!)
                                        }
                                    }
                                }
                            }
                        }
                        seasonEpisodes.forEach {
                            val items = MediaItem.fromUri(Uri.parse(it))
                            seasonEpisodesUri.add(items)
                        }
                        episodes.value = seasonEpisodesUri
                        player.value!!.addMediaItems(episodes.value!!)
                        Log.d("episodes", episodes.value.toString())
                    }
                    is Result.Error -> {
                        Log.d("errornextepisode", files.exception)
                    }
                }
            }
        }
    }
}