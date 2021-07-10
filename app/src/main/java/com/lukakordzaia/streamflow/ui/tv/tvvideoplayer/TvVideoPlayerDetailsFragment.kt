package com.lukakordzaia.streamflow.ui.tv.tvvideoplayer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lukakordzaia.streamflow.databinding.FragmentTvTitleFilesBinding
import com.lukakordzaia.streamflow.ui.baseclasses.BaseFragment
import com.lukakordzaia.streamflow.ui.tv.tvvideoplayer.TvVideoPlayerActivity.Companion.VIDEO_PLAYER

class TvVideoPlayerDetailsFragment : BaseFragment<FragmentTvTitleFilesBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentTvTitleFilesBinding
        get() = FragmentTvTitleFilesBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.goTopText.text = "უკან დაბრუნება"

        binding.tvDetailsGoTop.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                (requireActivity() as TvVideoPlayerActivity).setCurrentFragment(VIDEO_PLAYER)
                parentFragmentManager.popBackStack()
            }
        }
    }
}