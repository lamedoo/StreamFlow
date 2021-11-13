package com.lukakrodzaia.streamflowtv.baseclasses.activities

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import com.lukakordzaia.core.datamodels.ContinueWatchingModel
import com.lukakordzaia.core.utils.*
import com.lukakrodzaia.streamflowtv.R
import com.lukakrodzaia.streamflowtv.databinding.ActivityTvBaseBinding
import com.lukakrodzaia.streamflowtv.interfaces.TvCheckTitleSelected
import com.lukakrodzaia.streamflowtv.ui.genres.TvGenresActivity
import com.lukakrodzaia.streamflowtv.ui.main.TvActivity
import com.lukakrodzaia.streamflowtv.ui.tvcatalogue.TvCatalogueActivity
import com.lukakrodzaia.streamflowtv.ui.tvsingletitle.tvtitledetails.TvTitleDetailsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

abstract class BaseInfoFragmentActivity : BaseSidebarFragmentActivity<ActivityTvBaseBinding>(), TvCheckTitleSelected {
    private val tvTitleDetailsViewModel: TvTitleDetailsViewModel by viewModel()

    override fun getViewBinding() = ActivityTvBaseBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSidebar(binding.tvSidebar)
        setActiveButton()
    }

    private fun setActiveButton() {
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

    fun setProgressBar(isLoading: Boolean) {
        binding.progressBar.setVisibleOrGone(isLoading)
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
            binding.titleInfo.imdbScore.text = getString(R.string.imdb_score, it.imdbScore)
        })

        tvTitleDetailsViewModel.titleGenres.observe(this, {
            binding.titleInfo.genres.text = TextUtils.join(", ", it)
        })
    }
}