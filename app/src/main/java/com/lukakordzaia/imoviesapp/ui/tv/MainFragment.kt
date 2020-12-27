package com.lukakordzaia.imoviesapp.ui.tv

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import androidx.core.content.ContextCompat
import androidx.leanback.app.BackgroundManager
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.lifecycle.ViewModelProvider
import com.lukakordzaia.imoviesapp.R
import com.lukakordzaia.imoviesapp.ui.home.HomeViewModel

class MainFragment : BrowseSupportFragment() {
    private lateinit var viewModel: HomeViewModel
    lateinit var mCategoryRowAdapter: ArrayObjectAdapter
    lateinit var defaultBackground: Drawable
    lateinit var metrics: DisplayMetrics
    lateinit var backgroundManager: BackgroundManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        viewModel.getTopMovies()

        prepareBackgroundManager()
        setupUIElements()
//        setupEventListeners()
    }

    private fun prepareBackgroundManager() {
        backgroundManager = BackgroundManager.getInstance(activity).apply {
            attach(activity?.window)
        }
        defaultBackground = resources.getDrawable(R.drawable.main_background)
        metrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(metrics)
    }

    private fun setupUIElements() {
        badgeDrawable = resources.getDrawable(R.drawable.imovies_logo)
        // Badge, when set, takes precedent over title
        title = "IMOVIES"
        headersState = BrowseSupportFragment.HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true
        // set headers background color
        brandColor = ContextCompat.getColor(requireContext(), R.color.green_dark)

        // set search icon color
        searchAffordanceColor = ContextCompat.getColor(requireContext(), R.color.black)
    }

//    private fun setupEventListeners() {
//        setOnSearchClickedListener {
//            Intent(activity, SearchActivity::class.java).also { intent ->
//                startActivity(intent)
//            }
//        }
//
//        onItemViewClickedListener = ItemViewClickedListener()
//        onItemViewSelectedListener = ItemViewSelectedListener()
//    }
}