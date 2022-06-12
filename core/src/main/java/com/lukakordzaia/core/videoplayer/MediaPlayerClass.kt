package com.lukakordzaia.core.videoplayer

import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerView


class MediaPlayerClass(private val player: ExoPlayer) {

    fun initPlayer(playerView: PlayerView?, currentWindow: Int, playbackPosition: Long) {
        playerView?.player = player
        player.playWhenReady = true
        player.seekTo(currentWindow, playbackPosition)
        player.repeatMode = Player.REPEAT_MODE_OFF
        player.prepare()
        player.play()
    }

    fun setPlayerMediaSource(mediaSource: MediaItem) {
        player.addMediaItem(mediaSource)
    }

    fun setPlayerListener(event: Player.Listener) {
        player.addListener(event)
    }
}