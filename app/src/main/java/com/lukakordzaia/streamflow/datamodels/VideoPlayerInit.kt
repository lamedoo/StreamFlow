package com.lukakordzaia.streamflow.datamodels

data class VideoPlayerInfo(
        var currentWindow: Int,
        var playbackPosition: Long,
        var titleDuration: Long
)
