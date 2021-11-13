package com.lukakordzaia.streamflowtv.ui.tvsingletitle

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.lukakordzaia.core.AppConstants
import com.lukakordzaia.core.utils.applyBundle
import com.lukakordzaia.streamflowtv.R
import com.lukakordzaia.streamflowtv.baseclasses.activities.BaseFragmentActivity
import com.lukakordzaia.streamflowtv.databinding.ActivityTvSingleTitleBinding
import com.lukakordzaia.streamflowtv.ui.tvsingletitle.tvtitledetails.TvTitleDetailsFragment
import com.lukakordzaia.streamflowtv.ui.tvsingletitle.tvtitledetails.TvTitleDetailsViewModel
import com.lukakordzaia.streamflowtv.ui.tvsingletitle.tvtitlefiles.TvTitleFilesFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class TvSingleTitleActivity : BaseFragmentActivity<ActivityTvSingleTitleBinding>() {
    private val tvTitleDetailsViewModel: TvTitleDetailsViewModel by viewModel()

    override fun getViewBinding() = ActivityTvSingleTitleBinding.inflate(layoutInflater)

    private var currentFragment = TITLE_DETAILS

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setCurrentFragment(TvTitleDetailsFragment().applyBundle {
            putBoolean(AppConstants.CONTINUE_WATCHING_NOW, intent?.getSerializableExtra(AppConstants.CONTINUE_WATCHING_NOW) as? Boolean ?: false)
        })
    }

    override fun onBackPressed() {
        if (currentFragment == TITLE_FILES) {
            setCurrentFragment(TvTitleDetailsFragment())
        } else {
            super.onBackPressed()
        }
    }

    fun setCurrentFragment(fragment: Fragment) {
        currentFragment = when (fragment) {
            is TvTitleDetailsFragment -> TITLE_DETAILS
            is TvTitleFilesFragment -> TITLE_FILES
            else -> TITLE_DETAILS
        }

        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                if (currentFragment == TITLE_DETAILS) R.anim.slide_from_top else R.anim.slide_from_down,
                if (currentFragment == TITLE_DETAILS) R.anim.slide_out_down else R.anim.slide_out_top
            )
            .replace(R.id.tv_details_fr_nav_host, fragment)
            .commit()
    }

    companion object {
        const val TITLE_DETAILS = 0
        const val TITLE_FILES = 1
    }
}