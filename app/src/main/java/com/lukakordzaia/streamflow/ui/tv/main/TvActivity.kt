package com.lukakordzaia.streamflow.ui.tv.main

import android.os.Bundle
import android.text.TextUtils
import androidx.core.content.ContextCompat
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.databinding.ActivityTvBinding
import com.lukakordzaia.streamflow.datamodels.ContinueWatchingModel
import com.lukakordzaia.streamflow.interfaces.TvCheckTitleSelected
import com.lukakordzaia.streamflow.ui.baseclasses.BaseFragmentActivity
import com.lukakordzaia.streamflow.ui.tv.tvsingletitle.tvtitledetails.TvTitleDetailsViewModel
import com.lukakordzaia.streamflow.utils.setGone
import com.lukakordzaia.streamflow.utils.setImage
import com.lukakordzaia.streamflow.utils.setVisible
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit

class TvActivity : BaseFragmentActivity<ActivityTvBinding>(), TvCheckTitleSelected {
    private val tvTitleDetailsViewModel: TvTitleDetailsViewModel by viewModel()

    override fun getViewBinding() = ActivityTvBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSidebarClickListeners(binding.tvSidebar)
        setCurrentButton(binding.tvSidebar.homeButton)
        googleViews(binding.tvSidebar)

        binding.tvSidebar.homeButton.setOnClickListener {
            binding.tvSidebar.root.setGone()
        }

        binding.tvSidebarCollapsed.collapsedHomeIcon.setColorFilter(ContextCompat.getColor(this, R.color.accent_color))
    }

    override fun getTitleId(titleId: Int, continueWatchingDetails: ContinueWatchingModel?) {
        observeTitleDetails(titleId)

        if (continueWatchingDetails != null) {
            binding.titleInfo.continueWatchingSeekBar.setVisible()
            binding.titleInfo.continueWatchingSeason.setVisible()
            if (continueWatchingDetails.isTvShow) {
                binding.titleInfo.continueWatchingSeason.text = String.format("ს${continueWatchingDetails.season} ე${continueWatchingDetails.episode} / %02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(continueWatchingDetails.watchedDuration),
                        TimeUnit.MILLISECONDS.toSeconds(continueWatchingDetails.watchedDuration) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(continueWatchingDetails.watchedDuration))
                )
            } else {
                binding.titleInfo.continueWatchingSeason.text = String.format("%02d:%02d:%02d",
                        TimeUnit.MILLISECONDS.toHours(continueWatchingDetails.watchedDuration),
                        TimeUnit.MILLISECONDS.toMinutes(continueWatchingDetails.watchedDuration) -
                                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(continueWatchingDetails.watchedDuration)),
                        TimeUnit.MILLISECONDS.toSeconds(continueWatchingDetails.watchedDuration) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(continueWatchingDetails.watchedDuration))
                )
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