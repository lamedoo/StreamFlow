package com.lukakordzaia.streamflow.ui.tv.categories

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.widget.*
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.datamodels.TitleList
import com.lukakordzaia.streamflow.helpers.TvCheckFirstItem
import com.lukakordzaia.streamflow.ui.tv.details.TvDetailsActivity
import com.lukakordzaia.streamflow.ui.tv.main.presenters.TvCardPresenter
import com.lukakordzaia.streamflow.utils.AppConstants
import org.koin.androidx.viewmodel.ext.android.viewModel


class TvCategoriesFragment : VerticalGridSupportFragment() {
    private val gridAdapter = ArrayObjectAdapter(TvCardPresenter())
    private val tvCategoriesViewModel: TvCategoriesViewModel by viewModel()
    private var page = 1

    var onFirstItem: TvCheckFirstItem? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onFirstItem = context as? TvCheckFirstItem
    }

    override fun onDetach() {
        super.onDetach()
        onFirstItem = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = ""
        setupFragment()
            if (savedInstanceState == null) {
                prepareEntranceTransition()
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        when (activity?.intent?.getSerializableExtra("type") as Int) {
            AppConstants.TV_CATEGORY_NEW_MOVIES -> {
                badgeDrawable = resources.getDrawable(R.drawable.tv_new_movies_icon_full)
                tvCategoriesViewModel.getNewMoviesTv(page)
            }
            AppConstants.TV_CATEGORY_TOP_MOVIES -> {
                badgeDrawable = resources.getDrawable(R.drawable.tv_top_titles_icon_full)
                tvCategoriesViewModel.getTopMoviesTv(page)
            }
            AppConstants.TV_CATEGORY_TOP_TV_SHOWS -> {
                badgeDrawable = resources.getDrawable(R.drawable.tv_top_titles_icon_full)

                tvCategoriesViewModel.getTopTvShowsTv(page)
            }
        }

        Handler().postDelayed(Runnable {
            loadData()
            startEntranceTransition()
        }, 2000)
    }

    private fun loadData() {
        tvCategoriesViewModel.newMovieList.observe(viewLifecycleOwner, { newMovies ->
            newMovies.forEach {
                gridAdapter.add(it)
            }
        })
        tvCategoriesViewModel.topMovieList.observe(viewLifecycleOwner, { topMovies ->
            topMovies.forEach {
                gridAdapter.add(it)
            }
        })
        tvCategoriesViewModel.tvShowList.observe(viewLifecycleOwner, { topTvShows ->
            topTvShows.forEach {
                gridAdapter.add(it)
            }
        })
    }

    private fun setupFragment() {
        val gridPresenter = VerticalGridPresenter(FocusHighlight.ZOOM_FACTOR_LARGE, false).apply {
            numberOfColumns = 6
        }
        setGridPresenter(gridPresenter)
        adapter = gridAdapter

        onItemViewClickedListener = OnItemViewClickedListener { _, item, _, _ ->
            if (item is TitleList.Data) {
                val intent = Intent(context, TvDetailsActivity::class.java)
                intent.putExtra("titleId", item.id)
                intent.putExtra("isTvShow", item.isTvShow)
                activity?.startActivity(intent)
            }
        }
        setOnItemViewSelectedListener(ItemViewSelectedListener())
    }

    private inner class ItemViewSelectedListener : OnItemViewSelectedListener {
        override fun onItemSelected(itemViewHolder: Presenter.ViewHolder?, item: Any?, rowViewHolder: RowPresenter.ViewHolder?, row: Row?) {
            val indexOfRow = gridAdapter.size()
            val indexOfItem = gridAdapter.indexOf(item)

            if (indexOfItem == 0 || indexOfItem == 6) {
                onFirstItem?.isFirstItem(true)
            } else {
                onFirstItem?.isFirstItem(false)
            }

            if (indexOfItem != - 10 && indexOfRow - 10 <= indexOfItem) {
                page++
                when (activity?.intent?.getSerializableExtra("type") as Int) {
                    AppConstants.TV_CATEGORY_NEW_MOVIES -> {
                        tvCategoriesViewModel.getNewMoviesTv(page)
                    }
                    AppConstants.TV_CATEGORY_TOP_MOVIES -> {
                        tvCategoriesViewModel.getTopMoviesTv(page)
                    }
                    AppConstants.TV_CATEGORY_TOP_TV_SHOWS -> {
                        tvCategoriesViewModel.getTopTvShowsTv(page)
                    }
                }
            }
        }
    }
}