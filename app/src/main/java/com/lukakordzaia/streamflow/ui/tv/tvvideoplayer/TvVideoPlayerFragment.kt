package com.lukakordzaia.streamflow.ui.tv.tvvideoplayer

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.android.exoplayer2.util.Util
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.datamodels.VideoPlayerData
import com.lukakordzaia.streamflow.ui.baseclasses.BaseVideoPlayerFragment
import kotlinx.android.synthetic.main.tv_video_player_fragment.*

class TvVideoPlayerFragment : BaseVideoPlayerFragment(R.layout.tv_video_player_fragment) {
    private lateinit var videoPlayerData: VideoPlayerData

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setExoPlayer(tv_title_player)

        val titleId = activity?.intent?.getSerializableExtra("titleId") as Int
        val chosenLanguage = activity?.intent?.getSerializableExtra("chosenLanguage") as String
        val chosenSeason = activity?.intent?.getSerializableExtra("chosenSeason") as Int
        val isTvShow = activity?.intent?.getSerializableExtra("isTvShow") as Boolean
        val chosenEpisode = activity?.intent?.getSerializableExtra("chosenEpisode") as Int
        val watchedTime = activity?.intent?.getSerializableExtra("watchedTime") as Long
        val trailerUrl: String? = activity?.intent?.getSerializableExtra("trailerUrl") as String?

        videoPlayerData = VideoPlayerData(titleId, isTvShow, chosenSeason, chosenLanguage, chosenEpisode, watchedTime, trailerUrl)

        if (requireActivity().resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getPlayListFiles(videoPlayerData.titleId, videoPlayerData.chosenSeason, videoPlayerData.chosenLanguage)
        }
    }

    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT >= 24) {
            initPlayer(videoPlayerData.isTvShow, videoPlayerData.watchedTime, videoPlayerData.chosenEpisode, videoPlayerData.trailerUrl)
            Log.d("videoplaying", "started")
        }
    }

    override fun onResume() {
        super.onResume()
        if (Util.SDK_INT < 24) {
            initPlayer(videoPlayerData.isTvShow, videoPlayerData.watchedTime, videoPlayerData.chosenEpisode, videoPlayerData.trailerUrl)
        }
    }

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT < 24) {
            releasePlayer(videoPlayerData.titleId, videoPlayerData.isTvShow, videoPlayerData.chosenLanguage, videoPlayerData.trailerUrl)
            Log.d("videoplaying", "paused")
        }
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT >= 24) {
            releasePlayer(videoPlayerData.titleId, videoPlayerData.isTvShow, videoPlayerData.chosenLanguage, videoPlayerData.trailerUrl)
            Log.d("videoplaying", "stopped")
        }
    }
}