package com.lukakordzaia.imoviesapp.ui.baseclasses

import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.lukakordzaia.imoviesapp.R
import com.lukakordzaia.imoviesapp.ui.phone.videoplayer.VideoPlayerViewModel
import kotlinx.android.synthetic.main.exoplayer_controller_layout_new.*
import kotlinx.android.synthetic.main.fragment_video_player.*

open class BaseVideoPlayerFragment : Fragment(R.layout.fragment_video_player) {
    private lateinit var viewModel: VideoPlayerViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        viewModel = ViewModelProvider(this).get(VideoPlayerViewModel::class.java)

        viewModel.initHeader(header_tv)

        viewModel.setTitleName.observe(viewLifecycleOwner, { name ->
            viewModel.addEpisodeNames(name)
        })
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

    fun getPlayListFiles(titleId: Int, chosenSeason: Int, chosenLanguage: String) {
        viewModel.getPlaylistFiles(titleId, chosenSeason, chosenLanguage)
    }

    fun initPlayer(isTvShow: Boolean, watchedTime: Long, chosenEpisode: Int, trailerUrl: String?) {
        viewModel.initPlayer(requireContext(), title_player, isTvShow, watchedTime, chosenEpisode, trailerUrl)
    }

    fun releasePlayer(titleId: Int, isTvShow: Boolean, chosenLanguage: String, trailerUrl: String?) {
        viewModel.releasePlayer()
        if (trailerUrl == null) {
            viewModel.saveTitleToDb(requireContext(), titleId, isTvShow, chosenLanguage)
        }
    }
}