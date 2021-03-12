package com.lukakordzaia.streamflow.ui.tv.favorites

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.view.View
import androidx.core.content.ContextCompat
import androidx.leanback.app.BackgroundManager
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.*
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.datamodels.SingleTitleData
import com.lukakordzaia.streamflow.helpers.CustomListRowPresenter
import com.lukakordzaia.streamflow.helpers.TvCheckFirstItem
import com.lukakordzaia.streamflow.ui.phone.favorites.FavoritesViewModel
import com.lukakordzaia.streamflow.ui.tv.details.TvDetailsActivity
import com.lukakordzaia.streamflow.ui.tv.main.presenters.TvCardPresenter
import com.lukakordzaia.streamflow.ui.tv.main.presenters.TvHeaderItemPresenter
import org.koin.androidx.viewmodel.ext.android.viewModel

class TvFavoritesFragment : BrowseSupportFragment() {
    private val favoritesViewModel: FavoritesViewModel by viewModel()
    private lateinit var rowsAdapter: ArrayObjectAdapter
    lateinit var metrics: DisplayMetrics
    lateinit var backgroundManager: BackgroundManager

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
        if (savedInstanceState == null) {
            prepareEntranceTransition()
        }

        headersState = HEADERS_DISABLED

        setHeaderPresenterSelector(object : PresenterSelector() {
            override fun getPresenter(item: Any?): Presenter {
                return TvHeaderItemPresenter()
            }
        })

        val listRowPresenter = CustomListRowPresenter().apply {
            shadowEnabled = false
            selectEffectEnabled = false
        }
        rowsAdapter = ArrayObjectAdapter(listRowPresenter)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRowsAdapter()

        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            favoritesViewModel.getFavTitlesFromFirestore()
            startEntranceTransition()
        }, 2000)

        favoritesViewModel.movieResult.observe(viewLifecycleOwner, {
            movieRowsAdapter(it)
        })

        favoritesViewModel.tvShowResult.observe(viewLifecycleOwner, {
            tvShowsRowsAdapter(it)
        })

        prepareBackgroundManager()
        setupUIElements()
        setupEventListeners()
    }

    private fun initRowsAdapter() {
        val firstHeaderItem = ListRow(HeaderItem(0, "ფილმები"), ArrayObjectAdapter(TvCardPresenter()))
        val secondHeaderItem = ListRow(HeaderItem(1, "სერიალები"), ArrayObjectAdapter(TvCardPresenter()))
        val initListRows = mutableListOf(firstHeaderItem, secondHeaderItem)
        rowsAdapter.addAll(0, initListRows)
    }

    private fun movieRowsAdapter(movies: List<SingleTitleData.Data>) {
        val listRowAdapter = ArrayObjectAdapter(TvFavoritesPresenter()).apply {
            movies.forEach {
                add(it)
            }
        }

        HeaderItem(0, "ფილმები").also {
            rowsAdapter.replace(0, ListRow(it, listRowAdapter))
        }
    }

    private fun tvShowsRowsAdapter(tvShows: List<SingleTitleData.Data>) {
        val listRowAdapter = ArrayObjectAdapter(TvFavoritesPresenter()).apply {
            tvShows.forEach {
                add(it)
            }
        }

        HeaderItem(1, "სერიალები").also {
            rowsAdapter.replace(1, ListRow(it, listRowAdapter))
        }
    }

    private fun prepareBackgroundManager() {
        backgroundManager = BackgroundManager.getInstance(activity).apply {
            attach(activity?.window)
        }
        metrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(DisplayMetrics())
    }

    private fun setupUIElements() {
        isHeadersTransitionOnBackEnabled = true
        brandColor = ContextCompat.getColor(requireContext(), R.color.secondary_color)
        adapter = rowsAdapter
    }

    private fun setupEventListeners() {

        onItemViewClickedListener = ItemViewClickedListener()
        onItemViewSelectedListener = ItemViewSelectedListener()
    }

    private inner class ItemViewClickedListener : OnItemViewClickedListener {
        override fun onItemClicked(
                itemViewHolder: Presenter.ViewHolder,
                item: Any,
                rowViewHolder: RowPresenter.ViewHolder,
                row: Row
        ) {
            if (item is SingleTitleData.Data) {
                val intent = Intent(context, TvDetailsActivity::class.java)
                intent.putExtra("titleId", item.id)
                intent.putExtra("isTvShow", item.isTvShow)
                activity?.startActivity(intent)
            }
        }
    }

    private inner class ItemViewSelectedListener : OnItemViewSelectedListener {
        override fun onItemSelected(itemViewHolder: Presenter.ViewHolder?, item: Any?, rowViewHolder: RowPresenter.ViewHolder?, row: Row?) {
            val indexOfItem = ((row as ListRow).adapter as ArrayObjectAdapter).indexOf(item)

            if (indexOfItem == 0) {
                onFirstItem?.isFirstItem(true, rowsSupportFragment, rowsSupportFragment.selectedPosition)
            } else {
                onFirstItem?.isFirstItem(false, null, null)
            }
        }
    }
}