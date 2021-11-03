package com.lukakordzaia.streamflow.helpers.videoplayer

import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
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
            PlayerDurationInfo(player.currentPosition, player.duration)
        )
        player.release()
    }

    fun setPlayerMediaSource(mediaSource: MediaItem) {
        player.addMediaItem(mediaSource)
    }

    fun setPlayerListener(event: Player.Listener) {
        player.addListener(event)
    }
}