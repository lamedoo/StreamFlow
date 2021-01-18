package com.lukakordzaia.imoviesapp.ui.tv.main

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import androidx.core.content.ContextCompat
import androidx.leanback.app.BackgroundManager
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.*
import androidx.lifecycle.ViewModelProvider
import com.lukakordzaia.imoviesapp.R
import com.lukakordzaia.imoviesapp.network.datamodels.TitleList
import com.lukakordzaia.imoviesapp.network.datamodels.WatchedTitleData
import com.lukakordzaia.imoviesapp.ui.phone.home.HomeViewModel
import com.lukakordzaia.imoviesapp.ui.tv.details.TvDetailsActivity
import com.lukakordzaia.imoviesapp.ui.tv.search.TvSearchActivity
import com.lukakordzaia.imoviesapp.utils.setGone
import kotlinx.android.synthetic.main.fragment_home.*

class TvMainFragment : BrowseSupportFragment() {
    private lateinit var viewModel: HomeViewModel
    lateinit var mCategoryRowAdapter: ArrayObjectAdapter
    private val rowsAdapter = ArrayObjectAdapter(ListRowPresenter())
    lateinit var defaultBackground: Drawable
    lateinit var metrics: DisplayMetrics
    lateinit var backgroundManager: BackgroundManager

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

        viewModel.getWatchedFromDb(requireContext()).observe(viewLifecycleOwner, {
            if (!it.isNullOrEmpty()) {
                viewModel.getWatchedTitles(it)
            }
        })

        viewModel.watchedList.observe(viewLifecycleOwner, { watched ->
            watchedListRowsAdapter(watched)
        })

        viewModel.movieList.observe(viewLifecycleOwner, { movies ->
            topMoviesRowsAdapter(movies)
        })

        viewModel.tvShowList.observe(viewLifecycleOwner, { tvShows ->
            topTvShowsRowsAdapter(tvShows)
        })

        prepareBackgroundManager()
        setupUIElements()
        setupEventListeners()
    }

    private fun watchedListRowsAdapter(watchedList: List<WatchedTitleData>) {
        val listRowAdapter = ArrayObjectAdapter(WatchedCardPresenter()).apply {
            watchedList.forEach {
                add(it)
            }
        }

        HeaderItem(0, "განაგრძეთ ყურება").also { header ->
            rowsAdapter.add(ListRow(header, listRowAdapter))
        }
    }

    private fun topMoviesRowsAdapter(movies: List<TitleList.Data>) {
        val listRowAdapter = ArrayObjectAdapter(TvCardPresenter()).apply {
            movies.forEach {
                add(it)
            }
        }

        HeaderItem(1, "ტოპ ფილმები").also { header ->
            rowsAdapter.add(ListRow(header, listRowAdapter))
        }
    }

    private fun topTvShowsRowsAdapter(tvShows: List<TitleList.Data>) {
        val listRowAdapter = ArrayObjectAdapter(TvCardPresenter()).apply {
            tvShows.forEach {
                add(it)
            }
        }

        HeaderItem(2, "ტოპ სერიალები").also { header ->
            rowsAdapter.add(ListRow(header, listRowAdapter))
        }
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
        title = "IMOVIES"
        headersState = HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true
        brandColor = ContextCompat.getColor(requireContext(), R.color.green_dark)
        searchAffordanceColor = context?.let { ContextCompat.getColor(it, R.color.black) }!!
        adapter = rowsAdapter
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
            } else if (item is WatchedTitleData) {
                val intent = Intent(context, TvDetailsActivity::class.java)
                intent.putExtra("titleId", item.id)
                activity?.startActivity(intent)
            }
        }
    }
}