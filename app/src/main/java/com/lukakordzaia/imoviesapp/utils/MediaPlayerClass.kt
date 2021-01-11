package com.lukakordzaia.imoviesapp.utils

import android.content.Context
import android.util.Log
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import com.lukakordzaia.imoviesapp.network.datamodels.VideoPlayerOptions
import com.lukakordzaia.imoviesapp.network.datamodels.VideoPlayerRelease


class MediaPlayerClass {
    private lateinit var player: SimpleExoPlayer


    fun initPlayer(context: Context, playerView: PlayerView, playBackOptions: VideoPlayerOptions) {
        player = SimpleExoPlayer.Builder(context).build()
        playerView.player = player
        player.addListener(event)
        val position = playBackOptions.playbackPosition
        player.playWhenReady = playBackOptions.playWhenReady == true
        player.seekTo(playBackOptions.currentWindow, position)
        player.repeatMode = Player.REPEAT_MODE_OFF
        player.prepare()
        player.play()
    }

    fun releasePlayer(onRelease: (playBackOptions: VideoPlayerRelease) -> Unit) {
        onRelease(
            VideoPlayerRelease(
                player.playWhenReady,
                player.currentWindowIndex,
                player.currentPosition
            )
        )
        player.release()
    }

    fun addAllEpisodes(episodes: MutableList<MediaItem>) {
        player.addMediaItems(episodes)
        Log.d("mediaitems", "$episodes")
    }

    private val event = object : Player.EventListener {
        override fun onPlaybackStateChanged(state: Int) {
            super.onPlaybackStateChanged(state)
            if (state == Player.STATE_ENDED) {
                Log.d("playermessage", "done")
            }
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            super.onMediaItemTransition(mediaItem, reason)
            if (reason == Player.MEDIA_ITEM_TRANSITION_REASON_SEEK) {
                Log.d("playermessage", "changed to: ${mediaItem!!.mediaId}")
            }
        }
    }
}