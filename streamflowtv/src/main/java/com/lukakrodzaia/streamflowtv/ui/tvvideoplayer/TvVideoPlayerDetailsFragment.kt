package com.lukakrodzaia.streamflowtv.ui.tvvideoplayer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lukakordzaia.core.baseclasses.BaseFragment
import com.lukakrodzaia.streamflowtv.databinding.FragmentTvTitleFilesBinding
import com.lukakrodzaia.streamflowtv.ui.tvvideoplayer.TvVideoPlayerActivity.Companion.VIDEO_PLAYER

class TvVideoPlayerDetailsFragment : BaseFragment<FragmentTvTitleFilesBinding>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentTvTitleFilesBinding
        get() = FragmentTvTitleFilesBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.goTopText.text = "უკან დაბრუნება"

        binding.goTopText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                (requireActivity() as TvVideoPlayerActivity).setCurrentFragmentState(VIDEO_PLAYER)
                parentFragmentManager.popBackStack()
            }
        }
    }
}