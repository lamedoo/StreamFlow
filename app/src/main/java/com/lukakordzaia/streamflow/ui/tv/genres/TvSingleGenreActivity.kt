package com.lukakordzaia.streamflow.ui.tv.genres

import android.os.Bundle
import android.text.TextUtils
import androidx.core.content.ContextCompat
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.databinding.ActivityTvSingleGenreBinding
import com.lukakordzaia.streamflow.datamodels.DbTitleData
import com.lukakordzaia.streamflow.interfaces.TvCheckTitleSelected
import com.lukakordzaia.streamflow.ui.baseclasses.BaseFragmentActivity
import com.lukakordzaia.streamflow.ui.tv.details.titledetails.TvDetailsViewModel
import com.lukakordzaia.streamflow.utils.setGone
import com.lukakordzaia.streamflow.utils.setVisible
import com.squareup.picasso.Picasso
import org.koin.androidx.viewmodel.ext.android.viewModel

class TvSingleGenreActivity: BaseFragmentActivity<ActivityTvSingleGenreBinding>(), TvCheckTitleSelected {
    private val tvDetailsViewModel: TvDetailsViewModel by viewModel()

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

        tvDetailsViewModel.getSingleTitleResponse.observe(this, {
            binding.titleInfo.isTvShow.setVisible()
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
    }
}