package com.lukakordzaia.streamflowtv.ui.search

import android.os.Bundle
import android.text.TextUtils
import android.view.KeyEvent
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.lukakordzaia.core.datamodels.ContinueWatchingModel
import com.lukakordzaia.core.utils.hideKeyboard
import com.lukakordzaia.core.utils.setGone
import com.lukakordzaia.core.utils.showKeyboard
import com.lukakordzaia.streamflowtv.R
import com.lukakordzaia.streamflowtv.baseclasses.activities.BaseSidebarFragmentActivity
import com.lukakordzaia.streamflowtv.customviews.TvCustomSearchInput
import com.lukakordzaia.streamflowtv.databinding.ActivityTvSearchBinding
import com.lukakordzaia.streamflowtv.interfaces.TvTitleSelected
import com.lukakordzaia.streamflowtv.interfaces.TvSearchInputSelected
import com.lukakordzaia.streamflowtv.ui.tvsingletitle.tvtitledetails.TvTitleDetailsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class TvSearchActivity : BaseSidebarFragmentActivity<ActivityTvSearchBinding>(), TvSearchInputSelected, TvTitleSelected {
    private val tvTitleDetailsViewModel: TvTitleDetailsViewModel by viewModel()

    private lateinit var fragment: TvSearchFragment
    private var firstLoad = true
    private var searchInputSelected = true

    override fun getViewBinding() = ActivityTvSearchBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fragment = supportFragmentManager.findFragmentById(R.id.tv_search_fragment) as TvSearchFragment

        setSidebar(binding.tvSidebar)
        setCurrentButton(binding.tvSidebar.searchButton)

        binding.tvSidebarCollapsed.collapsedSearchIcon.setColorFilter(ContextCompat.getColor(this, R.color.accent_color))

        binding.titleInfo.backButtonInfo.root.setGone()
        searchInput()
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

        tvTitleDetailsViewModel.getSingleTitleResponse.observe(this, {
            binding.titleInfo.name.text = it.nameEng

            binding.titleInfo.year.text = "${it.releaseYear}"
            binding.titleInfo.duration.text = if (it.isTvShow) {
                "${it.seasonNum} სეზონი"
            } else {
                "${it.duration}"
            }

            binding.titleInfo.imdbScore.text = getString(R.string.imdb_score, it.imdbScore)
        })

        tvTitleDetailsViewModel.titleGenres.observe(this, {
            binding.titleInfo.genres.text = TextUtils.join(", ", it)
        })
    }
}