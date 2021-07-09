package com.lukakordzaia.streamflow.ui.tv.main

import android.os.Bundle
import android.text.TextUtils
import androidx.core.content.ContextCompat
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.databinding.ActivityTvBinding
import com.lukakordzaia.streamflow.datamodels.DbTitleData
import com.lukakordzaia.streamflow.interfaces.TvCheckTitleSelected
import com.lukakordzaia.streamflow.ui.baseclasses.BaseFragmentActivity
import com.lukakordzaia.streamflow.ui.tv.details.titledetails.TvDetailsViewModel
import com.lukakordzaia.streamflow.utils.setGone
import com.lukakordzaia.streamflow.utils.setVisible
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.tv_sidebar.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit

class TvActivity : BaseFragmentActivity<ActivityTvBinding>(), TvCheckTitleSelected {
    private val tvDetailsViewModel: TvDetailsViewModel by viewModel()

    override fun getViewBinding() = ActivityTvBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSidebarClickListeners(
                binding.tvSidebar.searchButton,
                binding.tvSidebar.homeButton,
                binding.tvSidebar.favoritesButton,
                binding.tvSidebar.moviesButton,
                binding.tvSidebar.genresButton,
                binding.tvSidebar.settingsButton
        )

        setCurrentButton(home_button)

        binding.tvSidebar.homeButton.setOnClickListener {
            binding.tvSidebar.root.setGone()
        }

        binding.tvSidebarCollapsed.collapsedHomeIcon.setColorFilter(ContextCompat.getColor(this, R.color.accent_color))

        googleSignIn(binding.tvSidebar.signIn)
        googleSignOut(binding.tvSidebar.signOut)
        googleProfileDetails(binding.tvSidebar.profilePhoto, binding.tvSidebar.profileUsername)

        tvDetailsViewModel.getSingleTitleResponse.observe(this, {
            binding.titleInfo.name.text = it.secondaryName
            if (it.covers?.data?.x1050!!.isNotBlank()) {
                Picasso.get().load(it.covers.data.x1050).error(R.drawable.movie_image_placeholder_landscape).into(binding.titleInfo.poster)
            }
            binding.titleInfo.year.text = "${it.year}   ·"
            if (it.isTvShow) {
                binding.titleInfo.duration.text = "${it.seasons?.data?.size} სეზონი   ·"
            } else {
                binding.titleInfo.duration.text = "${it.duration.toString()} წთ   ·"
            }
            if (it.rating.imdb?.score != null) {
                binding.titleInfo.imdbScore.text = "IMDB ${it.rating.imdb.score.toString()}"
            }
        })

        tvDetailsViewModel.titleGenres.observe(this, {
            binding.titleInfo.genres.text = TextUtils.join(", ", it)
        })
    }

    override fun getTitleId(titleId: Int, continueWatchingDetails: DbTitleData?) {
        tvDetailsViewModel.getSingleTitleData(titleId)

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
}