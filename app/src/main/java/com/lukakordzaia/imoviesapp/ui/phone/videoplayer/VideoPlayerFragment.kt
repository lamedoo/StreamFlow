package com.lukakordzaia.imoviesapp.ui.phone.videoplayer

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.google.android.exoplayer2.util.Util
import com.google.android.gms.cast.framework.CastButtonFactory
import com.lukakordzaia.imoviesapp.R
import com.lukakordzaia.imoviesapp.ui.baseclasses.BaseVideoPlayerFragment
import com.lukakordzaia.imoviesapp.utils.setGone
import kotlinx.android.synthetic.main.exoplayer_controller_layout.*
import kotlinx.android.synthetic.main.exoplayer_controller_layout_new.*
import kotlinx.android.synthetic.main.fragment_video_player.*


class VideoPlayerFragment : BaseVideoPlayerFragment() {
    private val args: VideoPlayerFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (requireActivity().resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getPlayListFiles(args.titleId, args.chosenSeason, args.chosenLanguage)
        }
    }

    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT >= 24) {
            initPlayer(args.isTvShow, args.watchedTime, args.chosenEpisode)
        }
    }

    override fun onResume() {
        super.onResume()
        if (Util.SDK_INT < 24) {
            initPlayer(args.isTvShow, args.watchedTime, args.chosenEpisode)
        }
    }

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT < 24) {
            releasePlayer(args.titleId, args.isTvShow, args.chosenLanguage)
        }
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT >= 24) {
            releasePlayer(args.titleId, args.isTvShow, args.chosenLanguage)
        }
    }
}