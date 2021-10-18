package com.lukakordzaia.streamflow.ui.phone.searchtitles

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lukakordzaia.streamflow.customviews.CustomSearchInput
import com.lukakordzaia.streamflow.databinding.FragmentPhoneSearchTitlesBinding
import com.lukakordzaia.streamflow.network.LoadingState
import com.lukakordzaia.streamflow.ui.baseclasses.BaseFragmentPhoneVM
import com.lukakordzaia.streamflow.utils.setGone
import com.lukakordzaia.streamflow.utils.setVisible
import com.xiaofeng.flowlayoutmanager.FlowLayoutManager
import kotlinx.android.synthetic.main.main_top_toolbar.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchTitlesFragment : BaseFragmentPhoneVM<FragmentPhoneSearchTitlesBinding, SearchTitlesViewModel>() {
    private var page = 1

    override val viewModel by viewModel<SearchTitlesViewModel>()
    override val reload: () -> Unit = {
        if (binding.searchTitleText.text.isNullOrEmpty()) {
            viewModel.getTopFranchises()
        } else {
            viewModel.getSearchTitles(binding.searchTitleText.text.toString(), page)
        }
    }

    private lateinit var searchTitlesAdapter: SearchTitlesAdapter
    private lateinit var topFranchisesAdapter: TopFranchisesAdapter
    private var pastVisibleItems: Int = 0
    private var visibleItemCount: Int = 0
    private var totalItemCount: Int = 0
    private var loading = false

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentPhoneSearchTitlesBinding
        get() = FragmentPhoneSearchTitlesBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        home_profile.setGone()

        fragmentObservers()
        searchInput()
        searchTitlesContainer()
        franchisesContainer()
    }

    private fun fragmentObservers() {
        viewModel.generalLoader.observe(viewLifecycleOwner, {
            when (it) {
                LoadingState.LOADING -> binding.searchProgressBar.setVisible()
                LoadingState.LOADED -> binding.searchProgressBar.setGone()
            }
        })
    }

    private fun searchInput() {
        binding.searchTitleText.setQueryTextChangeListener(object : CustomSearchInput.QueryTextListener {
            override fun onQueryTextSubmit(query: String?) {}


            override fun onQueryTextChange(newText: String?) {
                if (newText.isNullOrBlank()) {
                    binding.rvSearchTitlesContainer.setGone()
                    binding.topSearchContainer.setVisible()
                    viewModel.clearSearchResults()
                } else {
                    viewModel.clearSearchResults()
                    viewModel.getSearchTitles(newText, page)
                    binding.rvSearchTitlesContainer.setVisible()
                    binding.topSearchContainer.setGone()
                }
            }
        })
    }

    private fun searchTitlesContainer() {
        val layoutManager = LinearLayoutManager(requireActivity(), GridLayoutManager.VERTICAL, false)
        searchTitlesAdapter = SearchTitlesAdapter(requireContext()) {
            viewModel.onSingleTitlePressed(it)
        }

        binding.rvSearchTitles.adapter = searchTitlesAdapter
        binding.rvSearchTitles.layoutManager = layoutManager

        viewModel.searchList.observe(viewLifecycleOwner, {
            searchTitlesAdapter.setSearchTitleList(it)
        })

        binding.rvSearchTitles.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    visibleItemCount = layoutManager.childCount
                    totalItemCount = layoutManager.itemCount
                    pastVisibleItems = layoutManager.findFirstVisibleItemPosition()

                    if (!loading && (visibleItemCount + pastVisibleItems) >= totalItemCount) {
                        loading = true
                        fetchMoreResults()
                    }
                }
            }
        })
    }

    private fun franchisesContainer() {
        viewModel.getTopFranchises()

        topFranchisesAdapter = TopFranchisesAdapter(requireContext()) { titleName, position ->
            onFranchiseAnimationEnd(titleName)
        }
        binding.rvTopFranchises.layoutManager = FlowLayoutManager().apply {
            isAutoMeasureEnabled = true
        }
        binding.rvTopFranchises.adapter = topFranchisesAdapter

        viewModel.franchisesLoader.observe(viewLifecycleOwner, {
            when (it) {
                LoadingState.LOADING -> binding.franchisesProgressBar.setVisible()
                LoadingState.LOADED -> binding.franchisesProgressBar.setGone()
            }
        })

        viewModel.getTopFranchisesResponse.observe(viewLifecycleOwner, {
            topFranchisesAdapter.setFranchisesList(it)
        })
    }

    private fun fetchMoreResults() {
        page++
        viewModel.getSearchTitles(binding.searchTitleText.text.toString(), page)
        binding.searchProgressBar.setVisible()
        loading = false
    }

    private fun onFranchiseAnimationEnd(titleName: String) {
        (binding.searchTitleText as TextView).text = titleName
        viewModel.getSearchTitles(titleName, 1)
        binding.rvSearchTitlesContainer.setVisible()
        binding.topSearchContainer.setGone()
    }
}