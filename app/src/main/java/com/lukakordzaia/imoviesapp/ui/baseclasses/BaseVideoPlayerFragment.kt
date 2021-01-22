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
import com.lukakordzaia.imoviesapp.utils.MediaPlayerClass
import com.lukakordzaia.imoviesapp.utils.setGone
import kotlinx.android.synthetic.main.exoplayer_controller_layout_new.*
import kotlinx.android.synthetic.main.fragment_video_player.*

open class BaseVideoPlayerFragment : Fragment(R.layout.fragment_video_player) {
    private lateinit var viewModel: VideoPlayerViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        viewModel = ViewModelProvider(this).get(VideoPlayerViewModel::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requireActivity().window.setDecorFitsSystemWindows(false)
        } else {
            requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        }

        viewModel.setTitleName.observe(viewLifecycleOwner, { name ->
            viewModel.addEpisodeNames(header_tv, name)
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

    fun initPlayer(isTvShow: Boolean, watchedTime: Long, chosenEpisode: Int) {
        viewModel.initPlayer(requireContext(), title_player, isTvShow, watchedTime, chosenEpisode)
    }

    fun releasePlayer(titleId: Int, isTvShow: Boolean, chosenLanguage: String) {
        viewModel.releasePlayer()
        viewModel.saveTitleToDb(requireContext(), titleId, isTvShow, chosenLanguage)
    }
}