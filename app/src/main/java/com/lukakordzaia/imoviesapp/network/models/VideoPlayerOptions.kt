package com.lukakordzaia.imoviesapp.network.models

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
