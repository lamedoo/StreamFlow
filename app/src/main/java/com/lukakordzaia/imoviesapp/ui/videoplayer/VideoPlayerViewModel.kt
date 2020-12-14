package com.lukakordzaia.imoviesapp.ui.videoplayer

import android.content.Context
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import com.lukakordzaia.imoviesapp.ui.baseclasses.BaseViewModel

class VideoPlayerViewModel : BaseViewModel() {
    private var playWhenReady = MutableLiveData(true)
    private var currentWindow = MutableLiveData(0)
    private var playbackPosition = MutableLiveData<Long>(0)
    private var player = MutableLiveData<SimpleExoPlayer>()
    private var mediaLink = MutableLiveData<String>()

    fun initPlayer(context: Context, playerView: PlayerView, media: String){
        mediaLink.value = media
        val mediaUri = MediaItem.fromUri(Uri.parse(mediaLink.value))

        player.value = SimpleExoPlayer.Builder(context).build()
        playerView.player = player.value
        player.value?.playWhenReady = playWhenReady.value == true
        player.value?.setMediaItem(mediaUri)
        player.value?.seekTo(playbackPosition.value!!)
        player.value?.prepare()
        player.value?.play()

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
}