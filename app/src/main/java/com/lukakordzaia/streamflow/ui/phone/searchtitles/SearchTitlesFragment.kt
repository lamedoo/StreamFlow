package com.lukakordzaia.streamflow.ui.phone.searchtitles

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.animations.SearchAnimations
import com.lukakordzaia.streamflow.network.LoadingState
import com.lukakordzaia.streamflow.ui.customviews.SearchEditText
import com.lukakordzaia.streamflow.utils.*
import com.xiaofeng.flowlayoutmanager.FlowLayoutManager
import kotlinx.android.synthetic.main.phone_search_titles_framgent_new.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchTitlesFragment : Fragment(R.layout.phone_search_titles_framgent_new) {
    private val searchTitlesViewModel by viewModel<SearchTitlesViewModel>()
    private lateinit var searchTitlesAdapter: SearchTitlesAdapter
    private lateinit var topFranchisesAdapter: TopFranchisesAdapter
    private var page = 1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchTitlesViewModel.noInternet.observe(viewLifecycleOwner, EventObserver {
            if (it) {
                requireContext().createToast(AppConstants.NO_INTERNET)
                Handler(Looper.getMainLooper()).postDelayed({
                    searchTitlesViewModel.refreshContent()
                }, 5000)
            }
        })

        val layoutManager = LinearLayoutManager(requireActivity(), GridLayoutManager.VERTICAL, false)
        searchTitlesAdapter = SearchTitlesAdapter(requireContext()) {
            searchTitlesViewModel.onSingleTitlePressed(it)
        }

        rv_search_titles.adapter = searchTitlesAdapter
        rv_search_titles.layoutManager = layoutManager

        searchTitlesViewModel.searchLoader.observe(viewLifecycleOwner, {
            when (it.status) {
                LoadingState.Status.RUNNING -> search_progressBar.setVisible()
                LoadingState.Status.SUCCESS -> search_progressBar.setGone()
            }
        })

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


        // Franchises
        searchTitlesViewModel.getTopFranchises()

        topFranchisesAdapter = TopFranchisesAdapter(requireContext()) { titleName, position ->
            SearchAnimations().textTopTop(rv_top_franchises.getChildAt(position), search_titles_fragment, 500) { onFranchiseAnimationEnd(titleName) }
        }
        rv_top_franchises.layoutManager = FlowLayoutManager().apply {
            isAutoMeasureEnabled = true
        }
        rv_top_franchises.adapter = topFranchisesAdapter

        searchTitlesViewModel.franchisesLoader.observe(viewLifecycleOwner, {
            when (it.status) {
                LoadingState.Status.RUNNING -> franchises_progressBar.setVisible()
                LoadingState.Status.SUCCESS -> franchises_progressBar.setGone()
            }
        })

        searchTitlesViewModel.franchiseList.observe(viewLifecycleOwner, {
            topFranchisesAdapter.setFranchisesList(it)
        })

        searchTitlesViewModel.toastMessage.observe(viewLifecycleOwner, EventObserver {
            requireContext().createToast(it)
        })
    }

    private fun fetchMoreResults() {
        page++
        Log.d("currentpage", page.toString())
        searchTitlesViewModel.getSearchTitles(search_title_text.text.toString(), page)
        search_progressBar.setVisible()
    }

    private fun onFranchiseAnimationEnd(titleName: String) {
        (search_title_text as TextView).text = titleName
        searchTitlesViewModel.getSearchTitles(titleName, 1)
        rv_search_titles_container.setVisible()
        top_search_container.setGone()
    }
}