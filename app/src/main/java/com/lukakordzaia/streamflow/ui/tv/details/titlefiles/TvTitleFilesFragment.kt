package com.lukakordzaia.streamflow.ui.tv.details.titlefiles

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.ui.tv.details.titledetails.TvDetailsFragment
import kotlinx.android.synthetic.main.tv_title_files_fragment.*

class TvTitleFilesFragment : Fragment(R.layout.tv_title_files_fragment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tv_details_go_top.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                parentFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.slide_from_top, R.anim.slide_out_down)
                        .replace(R.id.tv_details_fr_nav_host, TvDetailsFragment())
                        .commit()
            }
        }
    }
}