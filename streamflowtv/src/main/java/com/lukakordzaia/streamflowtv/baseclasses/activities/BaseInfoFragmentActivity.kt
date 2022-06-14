package com.lukakordzaia.streamflowtv.baseclasses.activities

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import com.lukakordzaia.core.domain.domainmodels.ContinueWatchingModel
import com.lukakordzaia.core.domain.domainmodels.TvInfoModel
import com.lukakordzaia.core.utils.*
import com.lukakordzaia.streamflowtv.R
import com.lukakordzaia.streamflowtv.databinding.ActivityTvBaseBinding
import com.lukakordzaia.streamflowtv.interfaces.TvTitleSelected
import com.lukakordzaia.streamflowtv.ui.genres.TvGenresActivity
import com.lukakordzaia.streamflowtv.ui.main.TvActivity
import com.lukakordzaia.streamflowtv.ui.tvcatalogue.TvCatalogueActivity

abstract class BaseInfoFragmentActivity : BaseSidebarFragmentActivity<ActivityTvBaseBinding>(), TvTitleSelected {
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

    override fun getTitleId(info: TvInfoModel, continueWatchingDetails: ContinueWatchingModel?) {
        observeTitleDetails(info)

        if (continueWatchingDetails != null) {
            binding.titleInfo.continueHeader.setVisible()
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
            binding.titleInfo.continueHeader.setGone()
            binding.titleInfo.continueWatchingSeason.setGone()
            binding.titleInfo.continueWatchingSeekBar.setGone()
        }
    }

    private fun observeTitleDetails(info: TvInfoModel) {
        binding.titleInfo.name.text = info.nameEng

        binding.titleInfo.poster.setImage(info.cover, false)

        binding.titleInfo.year.text = "${info.releaseYear}"
        binding.titleInfo.duration.text = if (info.isTvShow) {
            getString(R.string.season_number, info.seasonNum.toString())
        } else {
            info.duration
        }
        binding.titleInfo.imdbScore.text = getString(R.string.imdb_score, info.imdbScore)

        binding.titleInfo.genres.text = info.genres?.let { TextUtils.join(", ", it) }
    }
}