package com.lukakordzaia.streamflow.ui.tv.watchlist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.widget.*
import com.lukakordzaia.streamflow.datamodels.SingleTitleModel
import com.lukakordzaia.streamflow.interfaces.TvCheckFirstItem
import com.lukakordzaia.streamflow.interfaces.TvCheckTitleSelected
import com.lukakordzaia.streamflow.interfaces.TvHasFavoritesListener
import com.lukakordzaia.streamflow.interfaces.TvWatchListTopRow
import com.lukakordzaia.streamflow.ui.shared.WatchlistViewModel
import com.lukakordzaia.streamflow.ui.tv.tvcatalogue.TvCataloguePresenter
import com.lukakordzaia.streamflow.ui.tv.tvsingletitle.TvSingleTitleActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class TvWatchlistFragment : VerticalGridSupportFragment() {
    private val watchlistViewModel: WatchlistViewModel by viewModel()
    private lateinit var gridAdapter: ArrayObjectAdapter
    private lateinit var type: String
    private var hasFocus = false

    private var page = 1

    var onTitleSelected: TvCheckTitleSelected? = null
    var onFirstItem: TvCheckFirstItem? = null
    var hasFavorites: TvHasFavoritesListener? = null
    var tvWatchListTopRow: TvWatchListTopRow? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onTitleSelected = context as? TvCheckTitleSelected
        onFirstItem = context as? TvCheckFirstItem
        hasFavorites = context as? TvHasFavoritesListener
        tvWatchListTopRow = context as? TvWatchListTopRow
    }

    override fun onDetach() {
        super.onDetach()
        onTitleSelected = null
        onFirstItem = null
        hasFavorites = null
        tvWatchListTopRow = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = ""
        setupFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args = arguments
        val bundledType = args?.getString("type")

        type = bundledType!!

        watchlistViewModel.getUserWatchlist(page, type)
        gridAdapter = ArrayObjectAdapter(TvCataloguePresenter())

        watchlistViewModel.noFavorites.observe(viewLifecycleOwner, {
            if (it) {
                hasFavorites?.hasFavorites(false)
            }
        })

        watchlistViewModel.userWatchlist.observe(viewLifecycleOwner, { watchlist ->
            watchlist.forEach {
                gridAdapter.add(it)
            }
        })

        adapter = gridAdapter
    }

    private fun setupFragment() {
        val gridPresenter = VerticalGridPresenter(FocusHighlight.ZOOM_FACTOR_NONE, false)
        gridPresenter.numberOfColumns = 6
        setGridPresenter(gridPresenter)

        onItemViewClickedListener = ItemViewClickedListener()
        setOnItemViewSelectedListener(ItemViewSelectedListener())
    }

    private inner class ItemViewClickedListener : OnItemViewClickedListener {
        override fun onItemClicked(
            itemViewHolder: Presenter.ViewHolder?,
            item: Any?,
            rowViewHolder: RowPresenter.ViewHolder?,
            row: Row?
        ) {
            if (item is SingleTitleModel) {
                val intent = Intent(context, TvSingleTitleActivity::class.java)
                intent.putExtra("titleId", item.id)
                intent.putExtra("isTvShow", item.isTvShow)
                activity?.startActivity(intent)
            }
        }
    }

    private inner class ItemViewSelectedListener : OnItemViewSelectedListener {
        override fun onItemSelected(itemViewHolder: Presenter.ViewHolder?, item: Any?, rowViewHolder: RowPresenter.ViewHolder?, row: Row?) {
            val indexOfRow = gridAdapter.size()
            val indexOfItem = gridAdapter.indexOf(item)

            if (item is SingleTitleModel) {
                onTitleSelected?.getTitleId(item.id, null)
            }

            val gridSize = Array(gridAdapter.size()) { i -> (i * 1) + 1 }.toList()

            onFirstItem?.isFirstItem(false, null, null)

            if (indexOfItem == 0) {
                onFirstItem?.isFirstItem(true, null, null)
            }

            if (indexOfItem in 0..5) {
                tvWatchListTopRow?.isTopRow(true)
            } else {
                tvWatchListTopRow?.isTopRow(false)
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
                watchlistViewModel.getUserWatchlist(page, type)
            }
        }
    }
}