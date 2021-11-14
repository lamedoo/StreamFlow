package com.lukakordzaia.streamflowtv.ui.tvsingletitle.tvtitlefiles

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lukakordzaia.core.baseclasses.BaseFragment
import com.lukakordzaia.streamflowtv.databinding.FragmentTvTitleFilesBinding
import com.lukakordzaia.streamflowtv.ui.tvsingletitle.TvSingleTitleActivity
import com.lukakordzaia.streamflowtv.ui.tvsingletitle.tvtitledetails.TvTitleDetailsFragment

class TvTitleFilesFragment : BaseFragment<FragmentTvTitleFilesBinding>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentTvTitleFilesBinding
        get() = FragmentTvTitleFilesBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.goTopText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                (requireActivity() as TvSingleTitleActivity).setCurrentFragment(TvTitleDetailsFragment())
            }
        }
    }
}