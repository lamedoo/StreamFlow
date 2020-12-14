package com.lukakordzaia.imoviesapp.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.lukakordzaia.imoviesapp.R

class VideoPlayerClass(private val context: Context, private val playerView: PlayerView, private val mediaLink: String) {
    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition: Long = 0
    private var player: SimpleExoPlayer? = null

    fun initPlayer(){
        Log.d("videoplayer", "currentwindow: ${currentWindow}, playbackPosition: $playbackPosition")
        if (player == null) {
            player = SimpleExoPlayer.Builder(context).build()
            playerView.player = player
            player?.playWhenReady = playWhenReady
            player?.seekTo(currentWindow, playbackPosition)
            player?.prepare(buildMediaSource(mediaLink), false,false)
        }
    }

    private fun buildMediaSource(mediaLink: String): MediaSource {
        val userAgent = Util.getUserAgent(playerView.context, playerView.context.getString(R.string.app_name))
        val dataSourceFactory = DefaultHttpDataSourceFactory(userAgent)

        return ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(mediaLink))
    }

    fun releasePlayer() {
        Log.d("videoplayer1", "currentwindow: ${currentWindow}, playbackPosition: $playbackPosition")
        if (player == null) {
            return
        }
        playWhenReady = player!!.playWhenReady
        playbackPosition = player!!.currentPosition
        currentWindow = player!!.currentWindowIndex
        player!!.release()
        player = null
    }
}