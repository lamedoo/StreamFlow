package com.lukakordzaia.streamflow.helpers.videoplayer

import android.content.Context
import android.net.Uri
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.Format
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.MergingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.SingleSampleMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.MimeTypes
import com.lukakordzaia.streamflow.datamodels.TitleMediaItemsUri

class BuildMediaSource(context: Context) {
    private val dataSourceFactory = DefaultDataSourceFactory(context, "sample")

    private val textFormat = Format.createTextSampleFormat(
        null,
        MimeTypes.TEXT_VTT,
        C.SELECTION_FLAG_DEFAULT,
        null
    )

    fun mediaSource(titleMediaItemsUri: TitleMediaItemsUri): MediaSource {
        return if (titleMediaItemsUri.titleSubUri == null) {
            val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(titleMediaItemsUri.titleFileUri)

            MergingMediaSource(mediaSource)
        } else {
            val subtitlesSource = SingleSampleMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.parse(titleMediaItemsUri.titleSubUri), textFormat, C.TIME_UNSET)

            val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(titleMediaItemsUri.titleFileUri)

            MergingMediaSource(mediaSource, subtitlesSource)
        }
    }
}