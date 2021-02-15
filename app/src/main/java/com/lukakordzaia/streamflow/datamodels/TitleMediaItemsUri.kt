package com.lukakordzaia.streamflow.datamodels

import com.google.android.exoplayer2.MediaItem

data class TitleMediaItemsUri(
        val titleFileUri: List<MediaItem>,
        val titleSubUri: List<String>
)