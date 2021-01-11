package com.lukakordzaia.imoviesapp.ui.tv.tvvideoplayer

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
import com.lukakordzaia.imoviesapp.ui.phone.videoplayer.VideoPlayerFragmentArgs
import com.lukakordzaia.imoviesapp.ui.phone.videoplayer.VideoPlayerViewModel
import com.lukakordzaia.imoviesapp.utils.setGone
import kotlinx.android.synthetic.main.exoplayer_controller_layout.*
import kotlinx.android.synthetic.main.fragment_tv_video_player.*
import kotlinx.android.synthetic.main.fragment_video_player.*
import kotlinx.android.synthetic.main.fragment_video_player.title_player
import kotlin.properties.Delegates

class TvVideoPlayerFragment : Fragment(R.layout.fragment_tv_video_player) {
    private lateinit var viewModel: VideoPlayerViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        viewModel = ViewModelProvider(this).get(VideoPlayerViewModel::class.java)
        val titleId = activity?.intent?.getSerializableExtra("titleId") as Int
        val chosenLanguage = activity?.intent?.getSerializableExtra("chosenLanguage") as String
        val chosenSeason = activity?.intent?.getSerializableExtra("chosenSeason") as Int
        val isTvShow = activity?.intent?.getSerializableExtra("isTvShow") as Boolean
        val chosenEpisode = activity?.intent?.getSerializableExtra("chosenEpisode") as Int
        val watchedTime = activity?.intent?.getSerializableExtra("watchedTime") as Long

        if (requireActivity().resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            viewModel.getPlaylistFiles(titleId, chosenSeason, chosenLanguage)
        }

        exo_episodes.setGone()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requireActivity().window.setDecorFitsSystemWindows(false)
        } else {
            requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        }

    }

    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT >= 24) {
            val isTvShow = activity?.intent?.getSerializableExtra("isTvShow") as Boolean
            val chosenEpisode = activity?.intent?.getSerializableExtra("chosenEpisode") as Int
            val watchedTime = activity?.intent?.getSerializableExtra("watchedTime") as Long
            viewModel.initPlayer(requireContext(), tv_title_player, isTvShow, watchedTime, chosenEpisode)
        }
    }

    override fun onResume() {
        super.onResume()
        if (Util.SDK_INT < 24) {
            val isTvShow = activity?.intent?.getSerializableExtra("isTvShow") as Boolean
            val chosenEpisode = activity?.intent?.getSerializableExtra("chosenEpisode") as Int
            val watchedTime = activity?.intent?.getSerializableExtra("watchedTime") as Long
            viewModel.initPlayer(requireContext(), tv_title_player, isTvShow, watchedTime, chosenEpisode)
        }
    }

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT < 24) {
            val titleId = activity?.intent?.getSerializableExtra("titleId") as Int
            val chosenLanguage = activity?.intent?.getSerializableExtra("chosenLanguage") as String
            val isTvShow = activity?.intent?.getSerializableExtra("isTvShow") as Boolean
            viewModel.releasePlayer()
            viewModel.saveTitleToDb(requireContext(), titleId, isTvShow, chosenLanguage)
        }
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT >= 24) {
            val titleId = activity?.intent?.getSerializableExtra("titleId") as Int
            val chosenLanguage = activity?.intent?.getSerializableExtra("chosenLanguage") as String
            val isTvShow = activity?.intent?.getSerializableExtra("isTvShow") as Boolean
            viewModel.releasePlayer()
            viewModel.saveTitleToDb(requireContext(), titleId, isTvShow, chosenLanguage)
        }
    }

    override fun onDetach() {
        super.onDetach()
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requireActivity().window.setDecorFitsSystemWindows(true)
        } else {
            requireActivity().window.decorView.systemUiVisibility = View.VISIBLE
        }
    }
}