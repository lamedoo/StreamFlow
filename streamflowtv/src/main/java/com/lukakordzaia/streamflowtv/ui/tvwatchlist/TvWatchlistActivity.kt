package com.lukakordzaia.streamflowtv.ui.tvwatchlist

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.KeyEvent
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.lukakordzaia.core.utils.AppConstants
import com.lukakordzaia.core.domain.domainmodels.ContinueWatchingModel
import com.lukakordzaia.core.domain.domainmodels.TvInfoModel
import com.lukakordzaia.core.utils.applyBundle
import com.lukakordzaia.core.utils.setDrawableBackground
import com.lukakordzaia.core.utils.setImage
import com.lukakordzaia.core.utils.setVisible
import com.lukakordzaia.streamflowtv.R
import com.lukakordzaia.streamflowtv.baseclasses.activities.BaseSidebarFragmentActivity
import com.lukakordzaia.streamflowtv.databinding.ActivityTvWatchlistBinding
import com.lukakordzaia.streamflowtv.interfaces.TvTitleSelected
import com.lukakordzaia.streamflowtv.interfaces.TvHasFavoritesListener
import com.lukakordzaia.streamflowtv.interfaces.TvIsVerticalFirstRow

class TvWatchlistActivity: BaseSidebarFragmentActivity<ActivityTvWatchlistBinding>(), TvTitleSelected, TvHasFavoritesListener, TvIsVerticalFirstRow {
    private var hasFavorites = true
    private var isTop = true
    private var type = AppConstants.WATCHLIST_MOVIES

    override fun getViewBinding() = ActivityTvWatchlistBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSidebar(binding.tvSidebar)
        setCurrentButton(binding.tvSidebar.favoritesButton)

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
                    }
                    else -> super.onKeyDown(keyCode, event)

                }
            }
            else -> return super.onKeyDown(keyCode, event)
        }
    }

    override fun getTitleId(info: TvInfoModel, continueWatchingDetails: ContinueWatchingModel?) {
        binding.titleInfo.name.text = info.displayName

        binding.titleInfo.poster.setImage(info.cover, false)

        binding.titleInfo.year.text = "${info.releaseYear}"
        binding.titleInfo.duration.text = if (info.isTvShow) {
            "${info.seasonNum} სეზონი"
        } else {
            "${info.duration}"
        }

        binding.titleInfo.imdbScore.text = getString(R.string.imdb_score, info.imdbScore)

        binding.titleInfo.genres.text = info.genres?.let { TextUtils.join(", ", it) }
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