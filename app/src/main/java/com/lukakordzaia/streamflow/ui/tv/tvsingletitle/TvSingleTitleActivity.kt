package com.lukakordzaia.streamflow.ui.tv.tvsingletitle

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.ui.tv.tvsingletitle.tvtitledetails.TvTitleDetailsFragment
import com.lukakordzaia.streamflow.ui.tv.tvsingletitle.tvtitledetails.TvTitleDetailsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class TvSingleTitleActivity : FragmentActivity() {
    private val tvTitleDetailsViewModel: TvTitleDetailsViewModel by viewModel()

    private var currentFragment = TITLE_INFO
    private val tvDetailsFragment = TvTitleDetailsFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tv_single_title)

        supportFragmentManager.beginTransaction()
                .replace(R.id.tv_details_fr_nav_host, tvDetailsFragment)
                .commit()
    }

    override fun onBackPressed() {
        if (currentFragment == TITLE_DETAILS) {
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.slide_from_top, R.anim.slide_out_down)
                .replace(R.id.tv_details_fr_nav_host, TvTitleDetailsFragment())
                .commit()
            setCurrentFragment(TITLE_INFO)
        } else {
            super.onBackPressed()
        }
    }

    fun setCurrentFragment(fragment: Int) {
        currentFragment = fragment
    }

    companion object {
        const val TITLE_INFO = 0
        const val TITLE_DETAILS = 1
    }
}