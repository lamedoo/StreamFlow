package com.lukakordzaia.streamflow.helpers

import android.util.Log
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import com.lukakordzaia.streamflow.datamodels.VideoPlayerInit
import com.lukakordzaia.streamflow.datamodels.VideoPlayerRelease


class MediaPlayerClass(private val player: SimpleExoPlayer) {

    fun initPlayer(playerView: PlayerView, playBackInit: VideoPlayerInit) {
        playerView.player = player
        val position = playBackInit.playbackPosition
        player.playWhenReady = true
        player.seekTo(playBackInit.currentWindow, position)
        player.repeatMode = Player.REPEAT_MODE_OFF
        player.prepare()
        player.play()
    }

    fun releasePlayer(onRelease: (playBackOptions: VideoPlayerRelease) -> Unit) {
        onRelease(
            VideoPlayerRelease(
                player.currentWindowIndex,
                player.currentPosition,
                player.duration
            )
        )
        player.release()
    }

    fun addAllEpisodes(episodes: MutableList<MediaItem>) {
        player.addMediaItems(episodes)
        Log.d("mediaitems", "$episodes")
    }

    fun setPlayerListener(event: Player.EventListener) {
        player.addListener(event)
    }

    fun setTrailerMediaItem(trailer: MediaItem) {
        player.addMediaItem(trailer)
    }
}