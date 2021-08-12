package com.lukakordzaia.streamflow.ui.tv.categories

import android.os.Bundle
import android.text.TextUtils
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.databinding.ActivityTvCategoriesBinding
import com.lukakordzaia.streamflow.datamodels.ContinueWatchingModel
import com.lukakordzaia.streamflow.interfaces.TvCheckTitleSelected
import com.lukakordzaia.streamflow.ui.baseclasses.BaseFragmentActivity
import com.lukakordzaia.streamflow.ui.tv.tvsingletitle.tvtitledetails.TvTitleDetailsViewModel
import com.lukakordzaia.streamflow.utils.setGone
import com.lukakordzaia.streamflow.utils.setImage
import org.koin.androidx.viewmodel.ext.android.viewModel

class TvCategoriesActivity : BaseFragmentActivity<ActivityTvCategoriesBinding>(), TvCheckTitleSelected {
    private val tvTitleDetailsViewModel: TvTitleDetailsViewModel by viewModel()

    override fun getViewBinding() = ActivityTvCategoriesBinding.inflate(layoutInflater)

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

        setCurrentButton(binding.tvSidebar.moviesButton)

        binding.tvSidebar.moviesButton.setOnClickListener {
            binding.tvSidebar.root.setGone()
        }

        binding.tvSidebarCollapsed.collapsedMoviesIcon.setColorFilter(ContextCompat.getColor(this, R.color.accent_color))

        googleSignIn(binding.tvSidebar.signIn)
        googleSignOut(binding.tvSidebar.signOut)
        googleProfileDetails(binding.tvSidebar.profilePhoto, binding.tvSidebar.profileUsername)

        tvTitleDetailsViewModel.getSingleTitleResponse.observe(this, {
            binding.titleInfo.name.text = it.nameEng

            binding.titleInfo.poster.setImage(it.cover, false)

            binding.titleInfo.year.text = "${it.releaseYear}   ·"
            if (it.isTvShow) {
                binding.titleInfo.duration.text = "${it.seasonNum} სეზონი   ·"
            } else {
                binding.titleInfo.duration.text = "${it.duration}   ·"
            }
            binding.titleInfo.imdbScore.text = "IMDB ${it.imdbScore}"
        })

        tvTitleDetailsViewModel.titleGenres.observe(this, {
            binding.titleInfo.genres.text = TextUtils.join(", ", it)
        })
    }

    override fun getTitleId(titleId: Int, continueWatchingDetails: ContinueWatchingModel?) {
        tvTitleDetailsViewModel.getSingleTitleData(titleId)
    }
}