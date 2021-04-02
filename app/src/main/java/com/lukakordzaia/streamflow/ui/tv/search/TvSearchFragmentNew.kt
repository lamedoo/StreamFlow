package com.lukakordzaia.streamflow.ui.tv.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.*
import com.lukakordzaia.streamflow.datamodels.TitleList
import com.lukakordzaia.streamflow.helpers.CustomListRowPresenter
import com.lukakordzaia.streamflow.helpers.TvCheckFirstItem
import com.lukakordzaia.streamflow.ui.phone.searchtitles.SearchTitlesViewModel
import com.lukakordzaia.streamflow.ui.tv.details.TvDetailsActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class TvSearchFragmentNew : BrowseSupportFragment() {
    private val rowsAdapter = ArrayObjectAdapter(CustomListRowPresenter())
    private val searchTitlesViewModel by viewModel<SearchTitlesViewModel>()
    private var page = 1
    private var searchQuery = ""

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
        onItemViewClickedListener = ItemViewClickedListener()
        onItemViewSelectedListener = ItemViewSelectedListener()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val query = (activity as TvSearchActivity).getSearchQuery()

            Log.d("searchquery", query)
    }

    private inner class ItemViewClickedListener : OnItemViewClickedListener {
        override fun onItemClicked(itemViewHolder: Presenter.ViewHolder, item: Any, rowViewHolder: RowPresenter.ViewHolder, row: Row) {
            if (item is TitleList.Data) {
                val intent = Intent(context, TvDetailsActivity::class.java)
                intent.putExtra("titleId", item.id)
                intent.putExtra("isTvShow", item.isTvShow)
                activity?.startActivity(intent)
            }
        }
    }

    private inner class ItemViewSelectedListener : OnItemViewSelectedListener {
        override fun onItemSelected(itemViewHolder: Presenter.ViewHolder?, item: Any?, rowViewHolder: RowPresenter.ViewHolder?, row: Row?) {
            val listRow = row as ListRow
            val currentRowAdapter: ArrayObjectAdapter = listRow.adapter as ArrayObjectAdapter
            val selectedIndex = currentRowAdapter.indexOf(item)

            if (selectedIndex <= 0) {
                onFirstItem?.isFirstItem(true, rowsSupportFragment, rowsSupportFragment?.selectedPosition)
            } else {
                onFirstItem?.isFirstItem(false, null, null)
            }

            if (selectedIndex != -1 && currentRowAdapter.size() - 1 == selectedIndex) {
                page++
                searchTitlesViewModel.getSearchTitlesTv(searchQuery, page)
            }
        }
    }
}