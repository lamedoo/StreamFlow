package com.lukakordzaia.streamflow.ui.tv.search

import android.os.Bundle
import android.text.TextUtils
import android.view.KeyEvent
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.customviews.TvCustomSearchInput
import com.lukakordzaia.streamflow.databinding.ActivityTvSearchBinding
import com.lukakordzaia.streamflow.datamodels.ContinueWatchingModel
import com.lukakordzaia.streamflow.interfaces.TvCheckTitleSelected
import com.lukakordzaia.streamflow.interfaces.TvSearchInputSelected
import com.lukakordzaia.streamflow.ui.baseclasses.BaseFragmentActivity
import com.lukakordzaia.streamflow.ui.tv.tvsingletitle.tvtitledetails.TvTitleDetailsViewModel
import com.lukakordzaia.streamflow.utils.hideKeyboard
import com.lukakordzaia.streamflow.utils.setGone
import com.lukakordzaia.streamflow.utils.showKeyboard
import org.koin.androidx.viewmodel.ext.android.viewModel

class TvSearchActivity : BaseFragmentActivity<ActivityTvSearchBinding>(), TvSearchInputSelected, TvCheckTitleSelected {
    private val tvTitleDetailsViewModel: TvTitleDetailsViewModel by viewModel()

    private lateinit var fragment: TvSearchFragmentNew
    private var firstLoad = true
    private var searchInputSelected = true

    override fun getViewBinding() = ActivityTvSearchBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fragment = supportFragmentManager.findFragmentById(R.id.tv_search_fragment) as TvSearchFragmentNew

        setSidebarClickListeners(
            binding.tvSidebar.searchButton,
            binding.tvSidebar.homeButton,
            binding.tvSidebar.favoritesButton,
            binding.tvSidebar.moviesButton,
            binding.tvSidebar.genresButton,
            binding.tvSidebar.settingsButton
        )

        setCurrentButton(binding.tvSidebar.searchButton)

        binding.tvSidebar.searchButton.setOnClickListener {
            binding.tvSidebar.root.setGone()
        }

        binding.tvSidebarCollapsed.collapsedSearchIcon.setColorFilter(ContextCompat.getColor(this, R.color.accent_color))

        googleSignIn(binding.tvSidebar.signIn)
        googleSignOut(binding.tvSidebar.signOut)
        googleProfileDetails(binding.tvSidebar.profilePhoto, binding.tvSidebar.profileUsername)

        searchInput()

        tvTitleDetailsViewModel.getSingleTitleResponse.observe(this, {
            binding.titleInfo.name.text = it.nameEng

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

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_DPAD_UP -> {
                if (searchInputSelected) {
                    binding.searchInput.showKeyboard()
                } else {
                    return super.onKeyDown(keyCode, event)
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onBackPressed() {
        if (!binding.searchInput.isFocused && !binding.tvSidebar.root.isVisible) {
            binding.searchInput.showKeyboard()
        } else {
            super.onBackPressed()
        }
    }

    private fun searchInput() {
        binding.root.viewTreeObserver.addOnGlobalLayoutListener {
            if (firstLoad) {
                binding.searchInput.showKeyboard()
                firstLoad = false
            }
        }

        binding.searchInput.setQueryTextChangeListener(object : TvCustomSearchInput.QueryTextListener {
            override fun onQueryTextSubmit(query: String?) {
                fragment.clearRowsAdapter()
                if (!query.isNullOrBlank()) {
                    fragment.setSearchQuery(query)
                }
                binding.searchInput.hideKeyboard()
            }

            override fun onQueryTextChange(newText: String?) {}
        })
    }

    override fun isSelected(selected: Boolean) {
        searchInputSelected = selected
    }

    override fun getTitleId(titleId: Int, continueWatchingDetails: ContinueWatchingModel?) {
        tvTitleDetailsViewModel.getSingleTitleData(titleId)
    }
}