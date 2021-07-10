package com.lukakordzaia.streamflow.ui.tv.search

import android.os.Bundle
import androidx.core.content.ContextCompat
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.customviews.TvCustomSearchInput
import com.lukakordzaia.streamflow.databinding.ActivityTvSearchBinding
import com.lukakordzaia.streamflow.ui.baseclasses.BaseFragmentActivity
import com.lukakordzaia.streamflow.utils.hideKeyboard
import com.lukakordzaia.streamflow.utils.setGone
import com.lukakordzaia.streamflow.utils.showKeyboard

class TvSearchActivity : BaseFragmentActivity<ActivityTvSearchBinding>() {
    private lateinit var fragment: TvSearchFragmentNew
    private var firstLoad = true

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
}