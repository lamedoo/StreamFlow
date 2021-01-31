package com.lukakordzaia.imoviesapp.datamodels

data class VideoPlayerInit(
        var playWhenReady: Boolean,
        var currentWindow: Int,
        var playbackPosition: Long,
)

data class VideoPlayerRelease(
        var playWhenReady: Boolean,
        var currentWindow: Int,
        var playbackPosition: Long,
)
