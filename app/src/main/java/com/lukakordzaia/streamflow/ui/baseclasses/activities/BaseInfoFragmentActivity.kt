package com.lukakordzaia.streamflow.ui.baseclasses.activities

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.databinding.ActivityTvBaseBinding
import com.lukakordzaia.streamflow.datamodels.ContinueWatchingModel
import com.lukakordzaia.streamflow.interfaces.TvCheckTitleSelected
import com.lukakordzaia.streamflow.ui.tv.genres.TvGenresActivity
import com.lukakordzaia.streamflow.ui.tv.main.TvActivity
import com.lukakordzaia.streamflow.ui.tv.tvcatalogue.TvCatalogueActivity
import com.lukakordzaia.streamflow.ui.tv.tvsingletitle.tvtitledetails.TvTitleDetailsViewModel
import com.lukakordzaia.streamflow.utils.setGone
import com.lukakordzaia.streamflow.utils.setImage
import com.lukakordzaia.streamflow.utils.setVisible
import com.lukakordzaia.streamflow.utils.titlePosition
import org.koin.androidx.viewmodel.ext.android.viewModel

abstract class BaseInfoFragmentActivity : BaseSidebarFragmentActivity<ActivityTvBaseBinding>(), TvCheckTitleSelected {
    private val tvTitleDetailsViewModel: TvTitleDetailsViewModel by viewModel()

    override fun getViewBinding() = ActivityTvBaseBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSidebar(binding.tvSidebar)
        setActiveButton()
    }

    fun setActiveButton() {
        when (this) {
            is TvActivity -> setCurrentButton(binding.tvSidebar.homeButton)
            is TvCatalogueActivity -> setCurrentButton(binding.tvSidebar.moviesButton)
            is TvGenresActivity -> setCurrentButton(binding.tvSidebar.genresButton)
        }


    }

    fun setFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_host, fragment)
            .commit()
    }

    override fun getTitleId(titleId: Int, continueWatchingDetails: ContinueWatchingModel?) {
        observeTitleDetails(titleId)

        if (continueWatchingDetails != null) {
            binding.titleInfo.continueWatchingSeekBar.setVisible()
            binding.titleInfo.continueWatchingSeason.setVisible()

            binding.titleInfo.continueWatchingSeason.text = if (continueWatchingDetails.isTvShow) {
                continueWatchingDetails.watchedDuration.titlePosition(continueWatchingDetails.season, continueWatchingDetails.episode)
            } else {
                continueWatchingDetails.watchedDuration.titlePosition(null, null)
            }

            binding.titleInfo.continueWatchingSeekBar.max = continueWatchingDetails.titleDuration.toInt()
            binding.titleInfo.continueWatchingSeekBar.progress = continueWatchingDetails.watchedDuration.toInt()
        } else {
            binding.titleInfo.continueWatchingSeason.setGone()
            binding.titleInfo.continueWatchingSeekBar.setGone()
        }
    }

    private fun observeTitleDetails(titleId: Int) {
        tvTitleDetailsViewModel.getSingleTitleData(titleId)

        tvTitleDetailsViewModel.getSingleTitleResponse.observe(this, {
            binding.titleInfo.name.text = it.nameEng

            binding.titleInfo.poster.setImage(it.cover, false)

            binding.titleInfo.year.text = "${it.releaseYear}"
            binding.titleInfo.duration.text = if (it.isTvShow) {
                "${it.seasonNum} სეზონი"
            } else {
                "${it.duration}"
            }
            binding.titleInfo.imdbScore.text = "IMDB ${it.imdbScore}"
        })

        tvTitleDetailsViewModel.titleGenres.observe(this, {
            binding.titleInfo.genres.text = TextUtils.join(", ", it)
        })
    }
}