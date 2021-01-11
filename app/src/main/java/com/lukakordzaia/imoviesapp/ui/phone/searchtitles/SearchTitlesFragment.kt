package com.lukakordzaia.imoviesapp.ui.phone.searchtitles

import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.lukakordzaia.imoviesapp.R
import com.lukakordzaia.imoviesapp.utils.*
import kotlinx.android.synthetic.main.fragment_search_titles.*

class SearchTitlesFragment : Fragment(R.layout.fragment_search_titles) {
    private lateinit var viewModel: SearchTitlesViewModel
    private lateinit var searchTitlesAdapter: SearchTitlesAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(SearchTitlesViewModel::class.java)

        searchTitlesAdapter = SearchTitlesAdapter(requireContext()) {
            viewModel.onSingleTitlePressed(it)
        }
        rv_search_titles.adapter =  searchTitlesAdapter

        search_title_text.setOnQueryTextListener(object  : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }


            override fun onQueryTextChange(query: String?): Boolean {
                if (!query.isNullOrBlank()) {
                    viewModel.getSearchTitles(query)
                    rv_search_titles.setVisible()
                    top_search_container.setGone()
                } else {
                    rv_search_titles.setGone()
                    top_search_container.setVisible()
                }
                return true
            }
        })

        viewModel.titleList.observe(viewLifecycleOwner, Observer {
            searchTitlesAdapter.setSearchTitleList(it)
        })

        viewModel.navigateScreen.observe(viewLifecycleOwner, EventObserver {
            navController(it)
        })

        viewModel.toastMessage.observe(viewLifecycleOwner, EventObserver {
            requireContext().createToast(it)
        })

    }
}