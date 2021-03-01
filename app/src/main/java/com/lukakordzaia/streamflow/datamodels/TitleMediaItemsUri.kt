package com.lukakordzaia.streamflow.datamodels

import com.google.android.exoplayer2.MediaItem

data class TitleMediaItemsUri(
        val titleFileUri: MediaItem,
        val titleSubUri: String
)