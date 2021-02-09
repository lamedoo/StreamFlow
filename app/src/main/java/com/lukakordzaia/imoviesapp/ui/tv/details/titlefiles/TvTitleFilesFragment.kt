package com.lukakordzaia.imoviesapp.ui.tv.details.titlefiles

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.lukakordzaia.imoviesapp.R
import com.lukakordzaia.imoviesapp.ui.tv.details.TvDetailsViewModel
import com.lukakordzaia.imoviesapp.ui.tv.details.titledetails.TvDetailsFragment
import kotlinx.android.synthetic.main.tv_title_files_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class TvTitleFilesFragment : Fragment(R.layout.tv_title_files_fragment) {
    private val tvDetailsViewModel by viewModel<TvDetailsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val titleId = activity?.intent?.getSerializableExtra("titleId") as Int
        val isTvShow = activity?.intent?.getSerializableExtra("isTvShow") as Boolean

        tvDetailsViewModel.getSingleTitleData(titleId)
        tvDetailsViewModel.getSingleTitleFiles(titleId)

        tv_details_go_top.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                parentFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.slide_from_top, R.anim.slide_out_down)
                        .replace(R.id.tv_details_fr_nav_host, TvDetailsFragment())
                        .show(TvDetailsFragment())
                        .commit()
            }
        }
    }
}