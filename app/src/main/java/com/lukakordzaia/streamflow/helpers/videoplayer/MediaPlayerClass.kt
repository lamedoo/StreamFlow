package com.lukakordzaia.streamflow.helpers.videoplayer

import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.lukakordzaia.streamflow.datamodels.PlayerDurationInfo


class MediaPlayerClass(private val player: SimpleExoPlayer) {

    fun initPlayer(playerView: PlayerView, currentWindow: Int, playbackPosition: Long) {
        playerView.player = player
        player.playWhenReady = true
        player.seekTo(currentWindow, playbackPosition)
        player.repeatMode = Player.REPEAT_MODE_OFF
        player.prepare()
        player.play()
    }

    fun releasePlayer(onRelease: (playBackOptions: PlayerDurationInfo) -> Unit) {
        onRelease(
            PlayerDurationInfo(
                player.currentPosition,
                player.duration
            )
        )
        player.release()
    }

    fun setMediaItems(episodes: List<MediaItem>) {
        player.addMediaItems(episodes)
    }

    fun setPlayerMediaSource(mediaSource: MediaSource) {
        player.addMediaSource(mediaSource)
    }

    fun setMultipleMediaSources(mediaSources: List<MediaSource>) {
        player.addMediaSources(mediaSources)
    }

    fun setPlayerListener(event: Player.EventListener) {
        player.addListener(event)
    }
}