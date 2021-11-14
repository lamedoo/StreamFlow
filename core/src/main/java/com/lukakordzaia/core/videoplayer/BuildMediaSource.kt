package com.lukakordzaia.core.videoplayer

import android.net.Uri
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.util.MimeTypes
import com.lukakordzaia.core.datamodels.TitleMediaItemsUri

class BuildMediaSource {
    fun mediaSource(titleMediaItemsUri: TitleMediaItemsUri): MediaItem {
        return if (titleMediaItemsUri.titleSubUri == null) {
            MediaItem.Builder()
                .setUri(Uri.parse(titleMediaItemsUri.titleFileUri))
                .build()
        } else {
            val subtitle = MediaItem.Subtitle(
                Uri.parse(titleMediaItemsUri.titleSubUri),
                MimeTypes.TEXT_VTT,
                null,
                C.SELECTION_FLAG_DEFAULT
            )

            MediaItem.Builder()
                .setUri(Uri.parse(titleMediaItemsUri.titleFileUri))
                .setSubtitles(listOf(subtitle))
                .build()
        }
    }
}