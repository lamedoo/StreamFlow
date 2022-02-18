package com.lukakordzaia.streamflowtv.ui.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.leanback.widget.*
import com.lukakordzaia.core.utils.AppConstants
import com.lukakordzaia.core.datamodels.SingleTitleModel
import com.lukakordzaia.core.network.toTvInfoModel
import com.lukakordzaia.streamflowtv.baseclasses.BaseVerticalGridSupportFragment
import com.lukakordzaia.streamflowtv.interfaces.TvSearchInputSelected
import com.lukakordzaia.streamflowtv.ui.tvsingletitle.TvSingleTitleActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class TvSearchFragment : BaseVerticalGridSupportFragment<TvSearchTitlesViewModel>() {
    private var searchQuery = ""

    override val viewModel by viewModel<TvSearchTitlesViewModel>()
    override val reload: () -> Unit = { viewModel.getSearchTitlesTv(searchQuery, page) }


    var searchInputIsSelected: TvSearchInputSelected? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        searchInputIsSelected = context as? TvSearchInputSelected
    }

    override fun onDetach() {
        super.onDetach()
        searchInputIsSelected = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.searchList.observe(viewLifecycleOwner, { list ->
            list.forEach {
                gridAdapter.add(it)
            }
        })

        setupEventListeners(ItemViewClickedListener(), ItemViewSelectedListener())
    }

    fun setSearchQuery(query: String) {
        searchQuery = query
        viewModel.getSearchTitlesTv(query, page)
    }

    fun clearRowsAdapter() {
        page = 1
        gridAdapter.clear()
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
                }
                activity?.startActivity(intent)
            }
        }

    }

    private inner class ItemViewSelectedListener : OnItemViewSelectedListener {
        override fun onItemSelected(itemViewHolder: Presenter.ViewHolder?, item: Any?, rowViewHolder: RowPresenter.ViewHolder?, row: Row?) {
            val indexOfRow = gridAdapter.size()
            val indexOfItem = gridAdapter.indexOf(item)

            if (item is SingleTitleModel) {
                onTitleSelected?.getTitleId(item.toTvInfoModel())
            }

            if (indexOfItem in 0 until 6) {
                searchInputIsSelected?.isSelected(true)
            } else {
                searchInputIsSelected?.isSelected(false)
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
                viewModel.getSearchTitlesTv(searchQuery, page)
            }
        }
    }
}