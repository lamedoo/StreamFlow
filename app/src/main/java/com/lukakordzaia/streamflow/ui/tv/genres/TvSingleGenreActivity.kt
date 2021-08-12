package com.lukakordzaia.streamflow.ui.tv.genres

import android.os.Bundle
import android.text.TextUtils
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.databinding.ActivityTvSingleGenreBinding
import com.lukakordzaia.streamflow.datamodels.ContinueWatchingModel
import com.lukakordzaia.streamflow.interfaces.TvCheckTitleSelected
import com.lukakordzaia.streamflow.ui.baseclasses.BaseFragmentActivity
import com.lukakordzaia.streamflow.ui.tv.tvsingletitle.tvtitledetails.TvTitleDetailsViewModel
import com.lukakordzaia.streamflow.utils.setGone
import com.lukakordzaia.streamflow.utils.setVisible
import org.koin.androidx.viewmodel.ext.android.viewModel

class TvSingleGenreActivity: BaseFragmentActivity<ActivityTvSingleGenreBinding>(), TvCheckTitleSelected {
    private val tvTitleDetailsViewModel: TvTitleDetailsViewModel by viewModel()

    override fun getViewBinding() = ActivityTvSingleGenreBinding.inflate(layoutInflater)

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

        setCurrentButton(binding.tvSidebar.genresButton)

        binding.tvSidebar.genresButton.setOnClickListener {
            binding.tvSidebar.root.setGone()
        }

        binding.tvSidebarCollapsed.collapsedGenresIcon.setColorFilter(ContextCompat.getColor(this, R.color.accent_color))

        googleSignIn(binding.tvSidebar.signIn)
        googleSignOut(binding.tvSidebar.signOut)
        googleProfileDetails(binding.tvSidebar.profilePhoto, binding.tvSidebar.profileUsername)

        supportFragmentManager.beginTransaction()
                .add(R.id.tv_single_genre_fragment, TvSingleGenreFragment())
                .show(TvSingleGenreFragment())
                .commit()

        tvTitleDetailsViewModel.getSingleTitleResponse.observe(this, {
            binding.titleInfo.isTvShow.setVisible()
            binding.titleInfo.name.text = it.nameEng

            Glide.with(this)
                .load(it.cover?: R.drawable.movie_image_placeholder)
                .placeholder(R.drawable.movie_image_placeholder_landscape)
                .into(binding.titleInfo.poster)

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