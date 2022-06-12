package com.lukakordzaia.core.videoplayer

import android.net.Uri
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.util.MimeTypes
import com.lukakordzaia.core.domain.domainmodels.TitleMediaItemsUri

class BuildMediaSource {
    fun mediaSource(titleMediaItemsUri: TitleMediaItemsUri): MediaItem {
        return if (titleMediaItemsUri.titleSubUri == null) {
            MediaItem.Builder()
                .setUri(Uri.parse(titleMediaItemsUri.titleFileUri))
                .build()
        } else {
            val subtitle = MediaItem.SubtitleConfiguration
                .Builder(Uri.parse(titleMediaItemsUri.titleSubUri))
                .setMimeType(MimeTypes.TEXT_VTT)
                .setSelectionFlags(C.SELECTION_FLAG_DEFAULT)
                .build()



            MediaItem.Builder()
                .setUri(Uri.parse(titleMediaItemsUri.titleFileUri))
                .setSubtitleConfigurations(listOf(subtitle))
                .build()
        }
    }
}