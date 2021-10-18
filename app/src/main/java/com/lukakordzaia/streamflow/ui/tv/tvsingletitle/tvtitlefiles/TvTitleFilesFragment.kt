package com.lukakordzaia.streamflow.ui.tv.tvsingletitle.tvtitlefiles

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.databinding.FragmentTvTitleFilesBinding
import com.lukakordzaia.streamflow.ui.baseclasses.fragments.BaseFragment
import com.lukakordzaia.streamflow.ui.tv.tvsingletitle.TvSingleTitleActivity
import com.lukakordzaia.streamflow.ui.tv.tvsingletitle.tvtitledetails.TvTitleDetailsFragment

class TvTitleFilesFragment : BaseFragment<FragmentTvTitleFilesBinding>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentTvTitleFilesBinding
        get() = FragmentTvTitleFilesBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvDetailsGoTop.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                (requireActivity() as TvSingleTitleActivity).setCurrentFragment(TvSingleTitleActivity.TITLE_INFO)
                parentFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.slide_from_top, R.anim.slide_out_down)
                        .replace(R.id.tv_details_fr_nav_host, TvTitleDetailsFragment())
                        .commit()
            }
        }
    }
}