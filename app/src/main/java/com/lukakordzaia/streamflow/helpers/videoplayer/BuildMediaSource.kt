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
import com.google.android.exoplayer2.util.Log
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

    fun movieMediaSource(titleMediaItemsUri: TitleMediaItemsUri): MediaSource {
        return if (titleMediaItemsUri.titleSubUri == "0") {
            val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(titleMediaItemsUri.titleFileUri)

            MergingMediaSource(mediaSource)
        } else {
            Log.d("mediaitems", titleMediaItemsUri.toString())
            val subtitlesSource = SingleSampleMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.parse(titleMediaItemsUri.titleSubUri), textFormat, C.TIME_UNSET)

            val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(titleMediaItemsUri.titleFileUri)

            MergingMediaSource(mediaSource, subtitlesSource)
        }
    }

    fun tvShowMediaSource(titleMediaItemsUri: List<TitleMediaItemsUri>): List<MediaSource> {
        Log.d("mediaitems", titleMediaItemsUri.toString())

        val episodes = mutableListOf<MediaSource>()
        val subtitles = mutableListOf<MediaSource?>()
        titleMediaItemsUri.forEach {
            val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(it.titleFileUri)
            episodes.add(mediaSource)

            if (it.titleSubUri != "0") {
                val subtitleSource = SingleSampleMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(Uri.parse(it.titleSubUri), textFormat, C.TIME_UNSET)
                subtitles.add(subtitleSource)
            } else {
                subtitles.add(null)
            }
        }

        val mergingMediaList: MutableList<MergingMediaSource> = ArrayList()

        var i = 0
        while (i < titleMediaItemsUri.size ) {
            if (subtitles[i] != null) {
                mergingMediaList.add(MergingMediaSource(episodes[i], subtitles[i]!!))
            } else {
                mergingMediaList.add(MergingMediaSource(episodes[i]))
            }
            i++
        }

//        Log.d("mediaitemssubtitles", subtitles.toString())
//        Log.d("mediaitemssubtitlessingle", subtitles[1].toString())

        return mergingMediaList
    }
}