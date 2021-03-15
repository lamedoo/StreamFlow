package com.lukakordzaia.streamflow.ui.phone.videoplayer

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.fragment.navArgs
import com.google.android.exoplayer2.util.Util
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.ui.baseclasses.BaseVideoPlayerFragment
import kotlinx.android.synthetic.main.phone_exoplayer_controller_layout.*
import kotlinx.android.synthetic.main.phone_fragment_video_player.*


open class VideoPlayerFragment : BaseVideoPlayerFragment(R.layout.phone_fragment_video_player) {
    private val args: VideoPlayerFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        phone_title_player.systemUiVisibility = (View.SYSTEM_UI_FLAG_LOW_PROFILE
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)

        setExoPlayer(phone_title_player)

        phone_exo_back.setOnClickListener {
            requireActivity().onBackPressed()
        }

        if (requireActivity().resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getPlayListFiles(args.videoPlayerData.titleId, args.videoPlayerData.chosenSeason, args.videoPlayerData.chosenLanguage, args.videoPlayerData.isTvShow)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            requireActivity().window.setDecorFitsSystemWindows(false)
//        } else {
//            requireActivity().window.decorView.apply {
//                systemUiVisibility =
//                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
//                                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
//                                View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//            }
//        }

//        requireActivity().window.decorView.apply {
//            systemUiVisibility =
//                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
//                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
//                        View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//        }
//
//        phone_title_player.systemUiVisibility = (View.SYSTEM_UI_FLAG_LOW_PROFILE
//                or View.SYSTEM_UI_FLAG_FULLSCREEN
//                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
    }

    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT >= 24) {
            if (requireActivity().resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                initPlayer(args.videoPlayerData.isTvShow, args.videoPlayerData.watchedTime, args.videoPlayerData.chosenEpisode, args.videoPlayerData.trailerUrl)
            }
            Log.d("videoplaying", "started")
        }
    }

    override fun onResume() {
        super.onResume()
        if (Util.SDK_INT < 24) {
            if (requireActivity().resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                initPlayer(args.videoPlayerData.isTvShow, args.videoPlayerData.watchedTime, args.videoPlayerData.chosenEpisode, args.videoPlayerData.trailerUrl)
            }
            Log.d("videoplaying", "resumed")
        }
    }

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT < 24) {
            releasePlayer(args.videoPlayerData.titleId, args.videoPlayerData.isTvShow, args.videoPlayerData.chosenLanguage, args.videoPlayerData.trailerUrl)
            Log.d("videoplaying", "paused")
        }
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT >= 24) {
            releasePlayer(args.videoPlayerData.titleId, args.videoPlayerData.isTvShow, args.videoPlayerData.chosenLanguage, args.videoPlayerData.trailerUrl)
            Log.d("videoplaying", "stopped")
        }
    }
}