package com.lukakordzaia.imoviesapp.ui.phone.videoplayer

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import com.google.android.exoplayer2.util.Util
import com.lukakordzaia.imoviesapp.R
import com.lukakordzaia.imoviesapp.ui.baseclasses.BaseVideoPlayerFragment
import kotlinx.android.synthetic.main.phone_fragment_video_player.*


open class VideoPlayerFragment : BaseVideoPlayerFragment(R.layout.phone_fragment_video_player) {
    private val args: VideoPlayerFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setExoPlayer(phone_title_player)

        if (requireActivity().resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getPlayListFiles(args.titleId, args.chosenSeason, args.chosenLanguage)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requireActivity().window.setDecorFitsSystemWindows(false)
        } else {
            requireActivity().window.decorView.apply {
                systemUiVisibility =
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                                View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT >= 24) {
            initPlayer(args.isTvShow, args.watchedTime, args.chosenEpisode, args.trailerUrl)
        }
    }

    override fun onResume() {
        super.onResume()
        if (Util.SDK_INT < 24) {
            initPlayer(args.isTvShow, args.watchedTime, args.chosenEpisode, args.trailerUrl)
        }
    }

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT < 24) {
            releasePlayer(args.titleId, args.isTvShow, args.chosenLanguage, args.trailerUrl)
        }
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT >= 24) {
            releasePlayer(args.titleId, args.isTvShow, args.chosenLanguage, args.trailerUrl)
        }
    }
}