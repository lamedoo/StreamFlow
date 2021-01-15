package com.lukakordzaia.imoviesapp.ui.tv.main

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.leanback.app.BackgroundManager
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.*
import androidx.lifecycle.ViewModelProvider
import com.lukakordzaia.imoviesapp.R
import com.lukakordzaia.imoviesapp.network.datamodels.TitleList
import com.lukakordzaia.imoviesapp.ui.phone.home.HomeViewModel
import com.lukakordzaia.imoviesapp.ui.tv.details.TvDetailsActivity
import com.lukakordzaia.imoviesapp.ui.tv.search.TvSearchActivity

class TvMainFragment : BrowseSupportFragment() {
    private lateinit var viewModel: HomeViewModel
    lateinit var mCategoryRowAdapter: ArrayObjectAdapter
    lateinit var defaultBackground: Drawable
    lateinit var metrics: DisplayMetrics
    lateinit var backgroundManager: BackgroundManager
    private lateinit var rowsAdapter: ArrayObjectAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        viewModel.getTopMovies()
        viewModel.getTopTvShows()

        setHeaderPresenterSelector(object : PresenterSelector() {
            override fun getPresenter(item: Any?): Presenter {
                return TvHeaderItemPresenter()
            }
        })

        viewModel.movieList.observe(viewLifecycleOwner, { movies ->
            viewModel.tvShowList.observe(viewLifecycleOwner, { tvShows ->
                buildRowsAdapter(movies, tvShows)
            })
        })

        prepareBackgroundManager()
        setupUIElements()
        setupEventListeners()
    }

    private fun buildRowsAdapter(movies: List<TitleList.Data>, tvShows: List<TitleList.Data>) {
        rowsAdapter = ArrayObjectAdapter(ListRowPresenter())

        val listRowAdapter = ArrayObjectAdapter(TvCardPresenter()).apply {
            movies.forEach {
                add(it)
            }
        }
        val listRowAdapter1 = ArrayObjectAdapter(TvCardPresenter()).apply {
            tvShows.forEach {
                add(it)
            }
        }
        HeaderItem(0, "ტოპ ფილმები").also { header ->
            rowsAdapter.add(ListRow(header, listRowAdapter))
        }
        HeaderItem(1, "ტოპ სერიალები").also { header ->
            rowsAdapter.add(ListRow(header, listRowAdapter1))
        }

        adapter = rowsAdapter
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
        headersState = HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true
        // set headers background color
        brandColor = ContextCompat.getColor(requireContext(), R.color.green_dark)

        // set search icon color
        searchAffordanceColor = context?.let { ContextCompat.getColor(it, R.color.black) }!!
    }

    private fun setupEventListeners() {
        setOnSearchClickedListener {
            startActivity(Intent(context, TvSearchActivity::class.java))
        }

        onItemViewClickedListener = ItemViewClickedListener()
    }

    private inner class ItemViewClickedListener : OnItemViewClickedListener {
        override fun onItemClicked(
                itemViewHolder: Presenter.ViewHolder,
                item: Any,
                rowViewHolder: RowPresenter.ViewHolder,
                row: Row
        ) {
            if (item is TitleList.Data) {
                val intent = Intent(context, TvDetailsActivity::class.java)
                intent.putExtra("titleId", item.id)
                activity?.startActivity(intent)
            }
        }
    }
}