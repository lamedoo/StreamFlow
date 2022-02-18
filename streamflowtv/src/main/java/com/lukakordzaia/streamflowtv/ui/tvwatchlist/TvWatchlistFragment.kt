package com.lukakordzaia.streamflowtv.ui.tvwatchlist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.leanback.widget.*
import com.lukakordzaia.core.utils.AppConstants
import com.lukakordzaia.core.network.toTvInfoModel
import com.lukakordzaia.core.domain.domainmodels.SingleTitleModel
import com.lukakordzaia.core.sharedpreferences.SharedPreferences
import com.lukakordzaia.streamflowtv.baseclasses.BaseVerticalGridSupportFragment
import com.lukakordzaia.streamflowtv.interfaces.TvHasFavoritesListener
import com.lukakordzaia.streamflowtv.interfaces.TvIsVerticalFirstRow
import com.lukakordzaia.streamflowtv.ui.tvsingletitle.TvSingleTitleActivity
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class TvWatchlistFragment : BaseVerticalGridSupportFragment<TvWatchlistViewModel>() {
    override val viewModel by viewModel<TvWatchlistViewModel>()
    private val sharedPreferences: SharedPreferences by inject()
    private lateinit var type: String

    override val reload: () -> Unit = {
        viewModel.getUserWatchlist(page, type, true)
    }

    private var selectedItem = 0

    var hasFavorites: TvHasFavoritesListener? = null
    var tvWatchListTopRow: TvIsVerticalFirstRow? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        hasFavorites = context as? TvHasFavoritesListener
        tvWatchListTopRow = context as? TvIsVerticalFirstRow
    }

    override fun onDetach() {
        super.onDetach()
        hasFavorites = null
        tvWatchListTopRow = null
    }

    override fun onStart() {
        super.onStart()

        if (sharedPreferences.getFromWatchlist() != -1) {
            gridAdapter.removeItems(sharedPreferences.getFromWatchlist(), 1)
            sharedPreferences.saveFromWatchlist(-1)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args = arguments
        val bundledType = args?.getString("type")
        type = bundledType!!

        viewModel.getUserWatchlist(page, type, true)

        viewModel.noFavorites.observe(viewLifecycleOwner, {
            if (it) {
                hasFavorites?.hasFavorites(false)
            }
        })

        viewModel.userWatchlist.observe(viewLifecycleOwner, { watchlist ->
            watchlist.forEach {
                gridAdapter.add(it)
            }
        })

        setupEventListeners(ItemViewClickedListener(), ItemViewSelectedListener())
    }

    private inner class ItemViewClickedListener : OnItemViewClickedListener {
        override fun onItemClicked(
            itemViewHolder: Presenter.ViewHolder?,
            item: Any?,
            rowViewHolder: RowPresenter.ViewHolder?,
            row: Row?
        ) {
            if (item is SingleTitleModel) {
                val intent = Intent(context, TvSingleTitleActivity::class.java).apply {
                    putExtra(AppConstants.TITLE_ID, item.id)
                    putExtra(AppConstants.IS_TV_SHOW, item.isTvShow)
                    putExtra(AppConstants.FROM_WATCHLIST, gridAdapter.indexOf(item))
                }
                activity?.startActivity(intent)
            }
        }
    }

    private inner class ItemViewSelectedListener : OnItemViewSelectedListener {
        override fun onItemSelected(itemViewHolder: Presenter.ViewHolder?, item: Any?, rowViewHolder: RowPresenter.ViewHolder?, row: Row?) {
            val indexOfRow = gridAdapter.size()
            val indexOfItem = gridAdapter.indexOf(item)

            selectedItem = indexOfItem

            onTitleSelected?.getTitleId((item as SingleTitleModel).toTvInfoModel())

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
                viewModel.getUserWatchlist(page, type, true)
            }
        }
    }
}