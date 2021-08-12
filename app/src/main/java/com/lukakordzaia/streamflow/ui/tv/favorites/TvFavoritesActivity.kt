package com.lukakordzaia.streamflow.ui.tv.favorites

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.databinding.ActivityTvFavoritesBinding
import com.lukakordzaia.streamflow.datamodels.ContinueWatchingModel
import com.lukakordzaia.streamflow.interfaces.TvCheckTitleSelected
import com.lukakordzaia.streamflow.interfaces.TvHasFavoritesListener
import com.lukakordzaia.streamflow.ui.baseclasses.BaseFragmentActivity
import com.lukakordzaia.streamflow.ui.tv.tvsingletitle.tvtitledetails.TvTitleDetailsViewModel
import com.lukakordzaia.streamflow.utils.createToast
import com.lukakordzaia.streamflow.utils.setGone
import com.lukakordzaia.streamflow.utils.setVisible
import kotlinx.android.synthetic.main.activity_tv_favorites.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class TvFavoritesActivity: BaseFragmentActivity<ActivityTvFavoritesBinding>(), TvCheckTitleSelected, TvHasFavoritesListener {
    private val tvTitleDetailsViewModel: TvTitleDetailsViewModel by viewModel()
    private var hasFavorites = true

    override fun getViewBinding() = ActivityTvFavoritesBinding.inflate(layoutInflater)

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

        setCurrentButton(binding.tvSidebar.favoritesButton)

        binding.tvSidebar.favoritesButton.setOnClickListener {
            binding.tvSidebar.root.setGone()
        }

        binding.tvSidebarCollapsed.collapsedFavoritesIcon.setColorFilter(ContextCompat.getColor(this, R.color.accent_color))

        googleSignIn(binding.tvSidebar.signIn)
        googleSignOut(binding.tvSidebar.signOut)
        googleProfileDetails(binding.tvSidebar.profilePhoto, binding.tvSidebar.profileUsername)

        Handler(Looper.getMainLooper()).postDelayed(Runnable {

            if (!hasFavorites) {
                no_favorites_container.setVisible()
                this.createToast("სამწუხაროდ, არ გაქვთ ფავორიტები არჩეული")
            }
        }, 2500)


        tvTitleDetailsViewModel.getSingleTitleResponse.observe(this, {
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

    override fun hasFavorites(has: Boolean) {
        hasFavorites = has
    }
}