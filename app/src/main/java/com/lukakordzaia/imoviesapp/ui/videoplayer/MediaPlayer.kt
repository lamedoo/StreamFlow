package com.lukakordzaia.imoviesapp.ui.videoplayer

import android.content.Context
import com.google.android.exoplayer2.ExoPlayer

interface MediaPlayer {

    fun play(url: String)
    fun getPlayer(context: Context): ExoPlayer
    fun releasePlayer()

}