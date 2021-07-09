package com.lukakordzaia.streamflow.ui.tv.categories

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.widget.*
import com.lukakordzaia.streamflow.network.models.imovies.response.titles.GetTitlesResponse
import com.lukakordzaia.streamflow.interfaces.TvCheckFirstItem
import com.lukakordzaia.streamflow.interfaces.TvCheckTitleSelected
import com.lukakordzaia.streamflow.ui.tv.details.TvDetailsActivity
import com.lukakordzaia.streamflow.utils.AppConstants
import org.koin.androidx.viewmodel.ext.android.viewModel


class TvCategoriesFragment : VerticalGridSupportFragment() {
    private val gridAdapter = ArrayObjectAdapter(TvCategoryPresenter())
    private val tvCategoriesViewModel: TvCategoriesViewModel by viewModel()
    private var page = 1

    var onTitleSelected: TvCheckTitleSelected? = null
    var onFirstItem: TvCheckFirstItem? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onTitleSelected = context as? TvCheckTitleSelected
        onFirstItem = context as? TvCheckFirstItem
    }

    override fun onDetach() {
        super.onDetach()
        onTitleSelected = null
        onFirstItem = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = ""
        setupFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
        loadData()
        adapter = gridAdapter
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
        val gridPresenter = VerticalGridPresenter(FocusHighlight.ZOOM_FACTOR_NONE, false)
        gridPresenter.numberOfColumns = 6
        setGridPresenter(gridPresenter)

        onItemViewClickedListener = OnItemViewClickedListener { _, item, _, _ ->
            if (item is GetTitlesResponse.Data) {
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

            if (item is GetTitlesResponse.Data) {
                onTitleSelected?.getTitleId(item.id, null)
            }

            val gridSize = Array(gridAdapter.size()) { i -> (i * 1) + 1 }.toList()

            onFirstItem?.isFirstItem(false, null, null)

            if (indexOfItem == 0) {
                onFirstItem?.isFirstItem(true, null, null)
            }

            gridSize.forEach {
                if (it % 6 == 0) {
                    if (indexOfItem == it) {
                        onFirstItem?.isFirstItem(true, null, null)
                    }
                }
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