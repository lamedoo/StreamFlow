package com.lukakordzaia.streamflow.helpers.videoplayer

import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.util.MimeTypes
import com.lukakordzaia.streamflow.datamodels.TitleMediaItemsUri

class BuildMediaSource {
    fun mediaSource(titleMediaItemsUri: TitleMediaItemsUri): MediaItem {
        return if (titleMediaItemsUri.titleSubUri == null) {
            MediaItem.Builder()
                .setUri(titleMediaItemsUri.titleFileUri)
                .build()
        } else {
            val subtitle = MediaItem.Subtitle(
                titleMediaItemsUri.titleSubUri,
                MimeTypes.TEXT_VTT,
                null,
                C.SELECTION_FLAG_DEFAULT
            )

            MediaItem.Builder()
                .setUri(titleMediaItemsUri.titleFileUri)
                .setSubtitles(listOf(subtitle))
                .build()
        }
    }
}