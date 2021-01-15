package com.lukakordzaia.imoviesapp.ui.tv.search

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.leanback.app.SearchSupportFragment
import androidx.leanback.widget.*
import androidx.lifecycle.ViewModelProvider
import com.lukakordzaia.imoviesapp.network.datamodels.TitleList
import com.lukakordzaia.imoviesapp.ui.phone.searchtitles.SearchTitlesViewModel
import com.lukakordzaia.imoviesapp.ui.tv.details.TvDetailsActivity
import com.lukakordzaia.imoviesapp.ui.tv.main.TvCardPresenter
import com.lukakordzaia.imoviesapp.utils.setGone
import com.lukakordzaia.imoviesapp.utils.setVisible
import kotlinx.android.synthetic.main.fragment_search_titles.*

class TvSearchFragment : SearchSupportFragment(), SearchSupportFragment.SearchResultProvider {
    private val rowsAdapter = ArrayObjectAdapter(ListRowPresenter())
    lateinit var viewModel: SearchTitlesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSearchResultProvider(this)
        setOnItemViewClickedListener(ItemViewClickedListener())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(SearchTitlesViewModel::class.java)

        viewModel.titleList.observe(viewLifecycleOwner, {
            val listRowAdapter = ArrayObjectAdapter(TvCardPresenter()).apply {
                it.forEach {
                    add(it)
                }
            }
            rowsAdapter.add(ListRow(listRowAdapter))
        })
    }

    override fun getResultsAdapter(): ObjectAdapter {
        return rowsAdapter
    }

    override fun onQueryTextChange(query: String): Boolean {
        return true
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        rowsAdapter.clear()
        if (!query.isNullOrBlank()) {
            viewModel.getSearchTitles(query)
        } else {
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
                activity?.startActivity(intent)
            }
        }
    }
}