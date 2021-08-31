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
import com.lukakordzaia.streamflow.ui.phone.phonewatchlist.PhoneWatchlistViewModel
import com.lukakordzaia.streamflow.ui.tv.tvcatalogue.TvCataloguePresenter
import com.lukakordzaia.streamflow.ui.tv.tvsingletitle.TvSingleTitleActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class TvWatchlistFragment : VerticalGridSupportFragment() {
    private val phoneWatchlistViewModel: PhoneWatchlistViewModel by viewModel()
    private lateinit var gridAdapter: ArrayObjectAdapter

    private var page = 1

    var onTitleSelected: TvCheckTitleSelected? = null
    var onFirstItem: TvCheckFirstItem? = null
    var hasFavorites: TvHasFavoritesListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onTitleSelected = context as? TvCheckTitleSelected
        onFirstItem = context as? TvCheckFirstItem
        hasFavorites = context as? TvHasFavoritesListener
    }

    override fun onDetach() {
        super.onDetach()
        onTitleSelected = null
        onFirstItem = null
        hasFavorites = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = ""
        setupFragment()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        phoneWatchlistViewModel.getUserWatchlist(page)
        gridAdapter = ArrayObjectAdapter(TvCataloguePresenter())

        phoneWatchlistViewModel.noFavorites.observe(viewLifecycleOwner, {
            if (it) {
                hasFavorites?.hasFavorites(false)
            }
        })

        phoneWatchlistViewModel.userWatchlist.observe(viewLifecycleOwner, { watchlist ->
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

            gridSize.forEach {
                if (it % 6 == 0) {
                    if (indexOfItem == it) {
                        onFirstItem?.isFirstItem(true, null, null)
                    }
                }
            }

            if (indexOfItem != - 10 && indexOfRow - 10 <= indexOfItem) {
                page++
                phoneWatchlistViewModel.getUserWatchlist(page)
            }
        }
    }
}