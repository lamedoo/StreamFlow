package com.lukakordzaia.streamflow.helpers.videoplayer

import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.lukakordzaia.streamflow.datamodels.VideoPlayerInfo
import com.lukakordzaia.streamflow.datamodels.VideoPlayerInit


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

    fun releasePlayer(onRelease: (playBackOptions: VideoPlayerInfo) -> Unit) {
        onRelease(
            VideoPlayerInfo(
                player.currentWindowIndex,
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