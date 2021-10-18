package com.lukakordzaia.streamflow.ui.tv.tvwatchlist

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.KeyEvent
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.databinding.ActivityTvWatchlistBinding
import com.lukakordzaia.streamflow.datamodels.ContinueWatchingModel
import com.lukakordzaia.streamflow.interfaces.TvCheckTitleSelected
import com.lukakordzaia.streamflow.interfaces.TvHasFavoritesListener
import com.lukakordzaia.streamflow.interfaces.TvWatchListTopRow
import com.lukakordzaia.streamflow.ui.baseclasses.BaseFragmentActivity
import com.lukakordzaia.streamflow.ui.tv.tvsingletitle.tvtitledetails.TvTitleDetailsViewModel
import com.lukakordzaia.streamflow.utils.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class TvWatchlistActivity: BaseFragmentActivity<ActivityTvWatchlistBinding>(), TvCheckTitleSelected, TvHasFavoritesListener, TvWatchListTopRow {
    private val tvTitleDetailsViewModel: TvTitleDetailsViewModel by viewModel()
    private var hasFavorites = true
    private var isTop = true
    private var type = AppConstants.WATCHLIST_MOVIES

    override fun getViewBinding() = ActivityTvWatchlistBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSidebarClickListeners(binding.tvSidebar)
        setCurrentButton(binding.tvSidebar.favoritesButton)
        googleViews(binding.tvSidebar)

        binding.tvSidebar.favoritesButton.setOnClickListener {
            binding.tvSidebar.root.setGone()
        }

        binding.tvSidebarCollapsed.collapsedFavoritesIcon.setColorFilter(ContextCompat.getColor(this, R.color.accent_color))

        buttonFocusability(true)

        fragmentType(AppConstants.WATCHLIST_MOVIES)

        binding.watchlistTvShows.setOnClickListener {
            fragmentType(AppConstants.WATCHLIST_TV_SHOWS)
        }

        binding.watchlistMovies.setOnClickListener {
            fragmentType(AppConstants.WATCHLIST_MOVIES)
        }

        Handler(Looper.getMainLooper()).postDelayed({
            if (!hasFavorites) {
                binding.noFavoritesContainer.setVisible()
            }
        }, 2500)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_DPAD_LEFT -> {
                when {
                    binding.watchlistTvShows.isFocused -> {
                        buttonFocusability(true)
                        binding.watchlistMovies.requestFocus()
                        true
                    }
                    binding.watchlistMovies.isFocused -> {
                        sidebarAnimations.showSideBar(binding.tvSidebar.tvSidebar)
                        binding.tvSidebar.favoritesButton.requestFocus()
                        true
                    }
                    else -> {
                        buttonFocusability(false)
                        super.onKeyDown(keyCode, event)
                    }
                }
            }
            KeyEvent.KEYCODE_DPAD_UP -> {
                when {
                    isTop -> {
                        buttonFocusability(!binding.tvSidebar.tvSidebar.isVisible)
                        if (type == AppConstants.WATCHLIST_MOVIES) binding.watchlistMovies.requestFocus() else binding.watchlistTvShows.requestFocus()
//                        true
                    }
                    else -> super.onKeyDown(keyCode, event)

                }
            }
            else -> return super.onKeyDown(keyCode, event)
        }
    }

    override fun getTitleId(titleId: Int, continueWatchingDetails: ContinueWatchingModel?) {
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

    override fun hasFavorites(has: Boolean) {
        hasFavorites = has
    }

    fun buttonFocusability(focusable: Boolean) {
        binding.watchlistMovies.apply {
            isFocusable = focusable
            isFocusableInTouchMode = focusable
        }

        binding.watchlistTvShows.apply {
            isFocusable = focusable
            isFocusableInTouchMode = focusable
        }
    }

    private fun fragmentType(type: String) {
        this.type = type

        when (type) {
            AppConstants.WATCHLIST_MOVIES -> {
                binding.watchlistMovies.setDrawableBackground(R.drawable.background_button_tv_catalogue)
                binding.watchlistTvShows.setDrawableBackground(R.drawable.background_button_tv)
            }
            AppConstants.WATCHLIST_TV_SHOWS -> {
                binding.watchlistMovies.setDrawableBackground(R.drawable.background_button_tv)
                binding.watchlistTvShows.setDrawableBackground(R.drawable.background_button_tv_catalogue)
            }
        }


        val fragment = TvWatchlistFragment().applyBundle {
            putString("type", type)
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.tv_watchlist_nav_host, fragment)
            .commit()
    }

    override fun isTopRow(isTop: Boolean) {
        this.isTop = isTop

    }
}