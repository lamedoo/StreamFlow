package com.lukakordzaia.streamflow.ui.phone.videoplayer

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.android.exoplayer2.util.Util
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.datamodels.VideoPlayerData
import com.lukakordzaia.streamflow.ui.baseclasses.BaseVideoPlayerFragment
import kotlinx.android.synthetic.main.phone_exoplayer_controller_layout.*
import kotlinx.android.synthetic.main.phone_fragment_video_player.*


open class VideoPlayerFragment : BaseVideoPlayerFragment(R.layout.phone_fragment_video_player) {
    private lateinit var videoPlayerData: VideoPlayerData


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        videoPlayerData = activity?.intent!!.getParcelableExtra<VideoPlayerData>("videoPlayerData")

        setExoPlayer(phone_title_player)

        phone_exo_back.setOnClickListener {
            requireActivity().onBackPressed()
        }

        if (requireActivity().resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getPlayListFiles(videoPlayerData.titleId, videoPlayerData.chosenSeason, videoPlayerData.chosenLanguage, videoPlayerData.isTvShow)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        requireActivity().window.decorView.apply {
            systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
    }

    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT >= 24) {
            if (requireActivity().resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                initPlayer(videoPlayerData.isTvShow, videoPlayerData.watchedTime, videoPlayerData.chosenEpisode, videoPlayerData.trailerUrl)
            }
            Log.d("videoplaying", "started")
        }
    }

    override fun onResume() {
        super.onResume()
        if (Util.SDK_INT < 24) {
            if (requireActivity().resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                initPlayer(videoPlayerData.isTvShow, videoPlayerData.watchedTime, videoPlayerData.chosenEpisode, videoPlayerData.trailerUrl)
            }
            Log.d("videoplaying", "resumed")
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