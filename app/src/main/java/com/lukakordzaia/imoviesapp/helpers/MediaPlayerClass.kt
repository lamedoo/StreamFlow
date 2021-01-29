package com.lukakordzaia.imoviesapp.helpers

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.TextView
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import com.lukakordzaia.imoviesapp.datamodels.VideoPlayerOptions
import com.lukakordzaia.imoviesapp.datamodels.VideoPlayerRelease


class MediaPlayerClass {
    private lateinit var player: SimpleExoPlayer
    private lateinit var header: TextView
    private var episodeNames: List<String> = listOf("Trailer")
    private val lastMediaListener: LastMediaPlaying? = null

    fun initPlayer(context: Context, playerView: PlayerView, playBackOptions: VideoPlayerOptions) {
        player = SimpleExoPlayer.Builder(context).build()
        playerView.player = player
        player.addListener(event)
        if (playBackOptions.trailerUrl != null) {
            Log.d("trailerurl", "${playBackOptions.trailerUrl}")
            player.addMediaItem(MediaItem.fromUri(Uri.parse(playBackOptions.trailerUrl)))
        }
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

    fun initHeader(header: TextView) {
        this.header = header
    }

    fun addEpisodeNames(episodeNames: List<String>) {
        this.episodeNames = episodeNames
    }

    fun addAllEpisodes(episodes: MutableList<MediaItem>) {
        player.addMediaItems(episodes)
        Log.d("mediaitems", "$episodes")
    }

    fun addNewEpisodes(episodes: MutableList<MediaItem>) {
        player.addMediaItems(episodes)
        Log.d("mediaitemsnew", "$episodes")
    }

    private val event = object : Player.EventListener {
        override fun onPlaybackStateChanged(state: Int) {
            super.onPlaybackStateChanged(state)
            if ((player.currentWindowIndex + 1) == player.mediaItemCount) {
                if (state == Player.STATE_READY) {
                    lastMediaListener?.isLastMedia(true)
                    Log.d("playermessage", "${player.currentWindowIndex}")
                }
            }
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            super.onMediaItemTransition(mediaItem, reason)
            header.text = episodeNames[player.currentWindowIndex]
        }
    }
}

interface LastMediaPlaying {
    fun isLastMedia(last: Boolean)
}