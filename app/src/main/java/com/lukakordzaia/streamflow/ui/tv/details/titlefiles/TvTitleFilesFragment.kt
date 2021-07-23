package com.lukakordzaia.streamflow.ui.tv.details.titlefiles

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.databinding.FragmentTvTitleFilesBinding
import com.lukakordzaia.streamflow.ui.baseclasses.BaseFragment
import com.lukakordzaia.streamflow.ui.tv.details.TvDetailsActivity
import com.lukakordzaia.streamflow.ui.tv.details.titledetails.TvDetailsFragment

class TvTitleFilesFragment : BaseFragment<FragmentTvTitleFilesBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentTvTitleFilesBinding
        get() = FragmentTvTitleFilesBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvDetailsGoTop.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                (requireActivity() as TvDetailsActivity).setCurrentFragment(TvDetailsActivity.TITLE_INFO)
                parentFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.slide_from_top, R.anim.slide_out_down)
                        .replace(R.id.tv_details_fr_nav_host, TvDetailsFragment())
                        .commit()
            }
        }
    }
}