package com.lukakordzaia.streamflowphone.ui.searchtitles

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lukakordzaia.core.network.LoadingState
import com.lukakordzaia.core.utils.setGone
import com.lukakordzaia.core.utils.setVisible
import com.lukakordzaia.core.utils.setVisibleOrGone
import com.lukakordzaia.streamflowphone.R
import com.lukakordzaia.streamflowphone.customviews.CustomSearchInput
import com.lukakordzaia.streamflowphone.databinding.FragmentPhoneSearchTitlesBinding
import com.lukakordzaia.streamflowphone.ui.baseclasses.BaseFragmentPhoneVM
import com.xiaofeng.flowlayoutmanager.FlowLayoutManager
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

        fragmentSetUi()
        fragmentObservers()
        searchInput()
    }

    private fun fragmentSetUi() {
        requireActivity().findViewById<ImageView>(R.id.home_profile).setGone()
        franchisesContainer()
        searchTitlesContainer()
    }

    private fun fragmentObservers() {
        viewModel.generalLoader.observe(viewLifecycleOwner, {
            binding.searchProgressBar.setVisibleOrGone(it == LoadingState.LOADING)
        })

        viewModel.franchisesLoader.observe(viewLifecycleOwner, {
            binding.franchisesProgressBar.setVisibleOrGone(it == LoadingState.LOADING)
        })

        viewModel.getTopFranchisesResponse.observe(viewLifecycleOwner, {
            topFranchisesAdapter.setFranchisesList(it)
        })

        viewModel.searchList.observe(viewLifecycleOwner, {
            searchTitlesAdapter.setSearchTitleList(it)
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

        topFranchisesAdapter = TopFranchisesAdapter(requireContext()) {
            onFranchiseClick(it)
        }
        binding.rvTopFranchises.layoutManager = FlowLayoutManager().apply {
            isAutoMeasureEnabled = true
        }
        binding.rvTopFranchises.adapter = topFranchisesAdapter
    }

    private fun fetchMoreResults() {
        page++
        viewModel.getSearchTitles(binding.searchTitleText.text.toString(), page)
        binding.searchProgressBar.setVisible()
        loading = false
    }

    private fun onFranchiseClick(titleName: String) {
        (binding.searchTitleText as TextView).text = titleName
        viewModel.getSearchTitles(titleName, 1)
        binding.rvSearchTitlesContainer.setVisible()
        binding.topSearchContainer.setGone()
    }
}