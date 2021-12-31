package com.lukakordzaia.streamflowtv.ui.tvsingletitle.tvepisodes

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.lukakordzaia.core.utils.AppConstants
import com.lukakordzaia.core.utils.applyBundle
import com.lukakordzaia.streamflowtv.R
import com.lukakordzaia.streamflowtv.baseclasses.activities.BaseFragmentActivity
import com.lukakordzaia.streamflowtv.databinding.ActivityTvSingleTitleBinding
import com.lukakordzaia.streamflowtv.ui.tvsingletitle.tvepisodes.TvEpisodesFragment
import com.lukakordzaia.streamflowtv.ui.tvsingletitle.tvepisodes.TvEpisodesViewModel
import com.lukakordzaia.streamflowtv.ui.tvsingletitle.tvtitledetails.TvTitleDetailsFragment
import com.lukakordzaia.streamflowtv.ui.tvsingletitle.tvtitledetails.TvTitleDetailsViewModel
import com.lukakordzaia.streamflowtv.ui.tvsingletitle.tvtitlerelated.TvRelatedFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class TvEpisodesActivity : BaseFragmentActivity<ActivityTvSingleTitleBinding>() {
    override fun getViewBinding() = ActivityTvSingleTitleBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setCurrentFragment(TvEpisodesFragment())
    }

    private fun setCurrentFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.tv_details_fr_nav_host, fragment)
            .commit()
    }

    companion object {
        fun newIntent(context: Context, titleId: Int): Intent {
            return Intent(context, TvEpisodesActivity::class.java).apply {
                putExtra(AppConstants.TITLE_ID, titleId)
            }
        }
    }
}