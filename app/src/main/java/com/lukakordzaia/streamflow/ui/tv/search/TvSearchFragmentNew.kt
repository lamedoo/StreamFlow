package com.lukakordzaia.streamflow.ui.tv.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.widget.*
import com.lukakordzaia.streamflow.datamodels.SingleTitleModel
import com.lukakordzaia.streamflow.interfaces.TvCheckFirstItem
import com.lukakordzaia.streamflow.interfaces.TvCheckTitleSelected
import com.lukakordzaia.streamflow.interfaces.TvSearchInputSelected
import com.lukakordzaia.streamflow.ui.phone.searchtitles.SearchTitlesViewModel
import com.lukakordzaia.streamflow.ui.tv.tvcatalogue.TvCataloguePresenter
import com.lukakordzaia.streamflow.ui.tv.tvsingletitle.TvSingleTitleActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class TvSearchFragmentNew : VerticalGridSupportFragment() {
    private val searchTitlesViewModel by viewModel<SearchTitlesViewModel>()
    private lateinit var gridAdapter: ArrayObjectAdapter

    private var page = 1
    private var searchQuery = ""

    var onTitleSelected: TvCheckTitleSelected? = null
    var onFirstItem: TvCheckFirstItem? = null
    var searchInputIsSelected: TvSearchInputSelected? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onTitleSelected = context as? TvCheckTitleSelected
        onFirstItem = context as? TvCheckFirstItem
        searchInputIsSelected = context as? TvSearchInputSelected
    }

    override fun onDetach() {
        super.onDetach()
        onTitleSelected = null
        onFirstItem = null
        searchInputIsSelected = null
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        gridAdapter = ArrayObjectAdapter(TvCataloguePresenter(requireContext()))

        searchTitlesViewModel.searchList.observe(viewLifecycleOwner, { list ->
            list.forEach {
                gridAdapter.add(it)
            }
        })

        adapter = gridAdapter
    }

    fun setSearchQuery(query: String) {
        searchQuery = query
        searchTitlesViewModel.getSearchTitlesTv(query, page)
    }

    fun clearRowsAdapter() {
        page = 1
        gridAdapter.clear()
    }

    fun clearSearchResults() {
        searchTitlesViewModel.clearSearchResults()
    }

    private fun setupFragment() {
        val gridPresenter = VerticalGridPresenter(FocusHighlight.ZOOM_FACTOR_NONE, false)
        gridPresenter.numberOfColumns = 6
        setGridPresenter(gridPresenter)

        onItemViewClickedListener = OnItemViewClickedListener { _, item, _, _ ->
            if (item is SingleTitleModel) {
                val intent = Intent(context, TvSingleTitleActivity::class.java)
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

            if (item is SingleTitleModel) {
                onTitleSelected?.getTitleId(item.id, null)
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
                searchTitlesViewModel.getSearchTitlesTv(searchQuery, page)
            }
        }
    }
}