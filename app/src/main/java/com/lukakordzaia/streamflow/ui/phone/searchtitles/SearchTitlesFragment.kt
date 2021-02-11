package com.lukakordzaia
.medootv.ui.phone.searchtitles

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.ui.customviews.SearchEditText
import com.lukakordzaia.streamflow.utils.*
import kotlinx.android.synthetic.main.phone_search_titles_framgent_new.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchTitlesFragment : Fragment(R.layout.phone_search_titles_framgent_new) {
    private val searchTitlesViewModel by viewModel<SearchTitlesViewModel>()
    private lateinit var searchTitlesAdapter: SearchTitlesAdapter
    private var page = 1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        searchTitlesAdapter = SearchTitlesAdapter(requireContext()) {
            searchTitlesViewModel.onSingleTitlePressed(it)
        }

        val layoutManager = LinearLayoutManager(requireActivity(), GridLayoutManager.VERTICAL, false)

        rv_search_titles.adapter = searchTitlesAdapter
        rv_search_titles.layoutManager = layoutManager

        search_title_text.setQueryTextChangeListener(object : SearchEditText.QueryTextListener {
            override fun onQueryTextSubmit(query: String?) {
                if (!query.isNullOrBlank()) {
                    searchTitlesViewModel.getSearchTitles(query, page)
                    rv_search_titles_container.setVisible()
                    top_search_container.setGone()
                } else {
                    searchTitlesViewModel.clearSearchResults()
                    rv_search_titles_container.setGone()
                    top_search_container.setVisible()
                }
            }


            override fun onQueryTextChange(newText: String?) {
                if (newText.isNullOrBlank()) {
                    rv_search_titles_container.setGone()
                    top_search_container.setVisible()
                    searchTitlesViewModel.clearSearchResults()
                } else {
                    rv_search_titles_container.setVisible()
                    top_search_container.setGone()
                }
            }
        })

        searchTitlesViewModel.searchList.observe(viewLifecycleOwner, Observer {
            searchTitlesAdapter.setSearchTitleList(it)
        })

        searchTitlesViewModel.navigateScreen.observe(viewLifecycleOwner, EventObserver {
            navController(it)
        })

        infiniteScroll(search_nested_scroll) {
            fetchMoreResults()
        }

        searchTitlesViewModel.toastMessage.observe(viewLifecycleOwner, EventObserver {
            requireContext().createToast(it)
        })
    }

    private fun fetchMoreResults() {
        page++
        Log.d("currentpage", page.toString())
        searchTitlesViewModel.getSearchTitles(search_title_text.text.toString(), page)
    }
}