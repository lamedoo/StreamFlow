package com.lukakordzaia.imoviesapp.network.datamodels

data class VideoPlayerOptions(
        var playWhenReady: Boolean,
        var currentWindow: Int,
        var playbackPosition: Long,
        var mediaLink: String?
)

data class VideoPlayerRelease(
        var playWhenReady: Boolean,
        var currentWindow: Int,
        var playbackPosition: Long,
)
