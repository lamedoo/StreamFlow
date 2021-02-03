package com.lukakordzaia.imoviesapp.ui.tv.search

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.leanback.app.SearchSupportFragment
import androidx.leanback.widget.*
import com.lukakordzaia.imoviesapp.datamodels.TitleList
import com.lukakordzaia.imoviesapp.ui.phone.searchtitles.SearchTitlesViewModel
import com.lukakordzaia.imoviesapp.ui.tv.details.TvDetailsActivity
import com.lukakordzaia.imoviesapp.ui.tv.main.TvCardPresenter
import org.koin.androidx.viewmodel.ext.android.viewModel


class TvSearchFragment : SearchSupportFragment(), SearchSupportFragment.SearchResultProvider {
    private val rowsAdapter = ArrayObjectAdapter(ListRowPresenter())
    private val searchTitlesViewModel by viewModel<SearchTitlesViewModel>()
    private var page = 1
    private var searchQuery = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSearchResultProvider(this)
        setOnItemViewClickedListener(ItemViewClickedListener())
        setOnItemViewSelectedListener(ItemViewSelectedListener())
        setSpeechRecognitionCallback {
            startActivityForResult(recognizerIntent, 0x00000010)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchTitlesViewModel.searchList.observe(viewLifecycleOwner, {
            val listRowAdapter = ArrayObjectAdapter(TvCardPresenter()).apply {
                it.forEach {
                    add(it)
                }
            }
            rowsAdapter.add(ListRow(listRowAdapter))
        })


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            0x00000010 -> {
                when (resultCode) {
                    Activity.RESULT_OK -> setSearchQuery(data, true)
                }
            }
        }
    }

    override fun getResultsAdapter(): ObjectAdapter {
        return rowsAdapter
    }

    override fun onQueryTextChange(query: String): Boolean {
        if (query.isNullOrBlank()) {
            searchTitlesViewModel.clearSearchResults()
        }
        return true
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        rowsAdapter.clear()
        if (!query.isNullOrBlank()) {
            searchQuery = query
            searchTitlesViewModel.getSearchTitles(query, page)
        } else {
            searchTitlesViewModel.clearSearchResults()
        }
        return true
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
            if (selectedIndex != -1 && currentRowAdapter.size() - 1 == selectedIndex) {
                page++
                searchTitlesViewModel.getSearchTitles(searchQuery, page)
            }
        }

    }
}