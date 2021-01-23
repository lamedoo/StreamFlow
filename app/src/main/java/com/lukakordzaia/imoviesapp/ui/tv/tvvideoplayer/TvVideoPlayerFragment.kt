package com.lukakordzaia.imoviesapp.ui.tv.tvvideoplayer

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.android.exoplayer2.util.Util
import com.lukakordzaia.imoviesapp.ui.baseclasses.BaseVideoPlayerFragment
import com.lukakordzaia.imoviesapp.ui.baseclasses.VideoPlayerData
import kotlinx.android.synthetic.main.fragment_video_player.*

class TvVideoPlayerFragment : BaseVideoPlayerFragment() {
    private lateinit var videoPlayerData: VideoPlayerData

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val titleId = activity?.intent?.getSerializableExtra("titleId") as Int
        val chosenLanguage = activity?.intent?.getSerializableExtra("chosenLanguage") as String
        val chosenSeason = activity?.intent?.getSerializableExtra("chosenSeason") as Int
        val isTvShow = activity?.intent?.getSerializableExtra("isTvShow") as Boolean
        val chosenEpisode = activity?.intent?.getSerializableExtra("chosenEpisode") as Int
        val watchedTime = activity?.intent?.getSerializableExtra("watchedTime") as Long

        videoPlayerData = VideoPlayerData(titleId, isTvShow, chosenSeason, chosenLanguage, chosenEpisode, watchedTime)

        if (requireActivity().resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getPlayListFiles(videoPlayerData.titleId, videoPlayerData.chosenSeason, videoPlayerData.chosenLanguage)
        }
    }

    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT >= 24) {
            initPlayer(videoPlayerData.isTvShow, videoPlayerData.watchedTime, videoPlayerData.chosenEpisode)
            Log.d("videoplaying", "started")
        }
    }

    override fun onResume() {
        super.onResume()
        if (Util.SDK_INT < 24) {
            initPlayer(videoPlayerData.isTvShow, videoPlayerData.watchedTime, videoPlayerData.chosenEpisode)
        }
    }

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT < 24) {
            releasePlayer(videoPlayerData.titleId, videoPlayerData.isTvShow, videoPlayerData.chosenLanguage)
            Log.d("videoplaying", "paused")
        }
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT >= 24) {
            releasePlayer(videoPlayerData.titleId, videoPlayerData.isTvShow, videoPlayerData.chosenLanguage)
            Log.d("videoplaying", "stopped")
        }
    }
}