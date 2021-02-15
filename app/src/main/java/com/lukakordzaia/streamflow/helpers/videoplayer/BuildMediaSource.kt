package com.lukakordzaia.streamflow.helpers.videoplayer

import android.content.Context
import android.net.Uri
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.Format
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.MergingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.SingleSampleMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.MimeTypes

class BuildMediaSource(context: Context){
    private val dataSourceFactory = DefaultDataSourceFactory(context, "sample")

    private val textFormat = Format.createTextSampleFormat(
            null,
            MimeTypes.TEXT_VTT,
            C.SELECTION_FLAG_DEFAULT,
            null
    )

    fun movieMediaSource(mediaItems: List<MediaItem>, subtitles: List<String>): MediaSource {
        return if (subtitles[0] == "0") {
            val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(mediaItems[0])

            MergingMediaSource(mediaSource,)
        } else {
            val subtitlesSource = SingleSampleMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(Uri.parse(subtitles[0]), textFormat, C.TIME_UNSET)

            val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(mediaItems[0])

            MergingMediaSource(mediaSource, subtitlesSource)
        }
    }

    fun tvShowMediaSource(mediaItems: List<MediaItem>, subtitles: List<String>): List<MediaSource> {
        val episodes = mutableListOf<MergingMediaSource>()
        mediaItems.forEach {
            val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(it)
            episodes.add(MergingMediaSource(mediaSource))
        }
        return episodes
    }
}