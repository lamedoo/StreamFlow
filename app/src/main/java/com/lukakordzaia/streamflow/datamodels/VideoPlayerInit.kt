package com.lukakordzaia.streamflow.datamodels

data class VideoPlayerInit(
        var currentWindow: Int,
        var playbackPosition: Long,
)

data class VideoPlayerRelease(
        var currentWindow: Int,
        var playbackPosition: Long,
        var titleDuration: Long
)
