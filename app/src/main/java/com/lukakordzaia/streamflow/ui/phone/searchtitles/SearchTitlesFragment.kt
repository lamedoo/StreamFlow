package com.lukakordzaia.streamflow.ui.phone.searchtitles

import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.lukakordzaia.streamflow.ui.baseclasses.BaseVMFragment
import com.lukakordzaia.streamflow.utils.*
import com.xiaofeng.flowlayoutmanager.FlowLayoutManager
import kotlinx.android.synthetic.main.main_top_toolbar.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchTitlesFragment : BaseVMFragment<FragmentPhoneSearchTitlesBinding, SearchTitlesViewModel>() {
    override val viewModel by viewModel<SearchTitlesViewModel>()

    private lateinit var searchTitlesAdapter: SearchTitlesAdapter
    private lateinit var topFranchisesAdapter: TopFranchisesAdapter
    private var page = 1
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
        viewModel.noInternet.observe(viewLifecycleOwner, EventObserver {
            if (it) {
                requireContext().createToast(AppConstants.NO_INTERNET)
                Handler(Looper.getMainLooper()).postDelayed({
                    viewModel.refreshContent()
                }, 5000)
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
        viewModel.searchLoader.observe(viewLifecycleOwner, {
            when (it.status) {
                LoadingState.Status.RUNNING -> binding.searchProgressBar.setVisible()
                LoadingState.Status.SUCCESS -> binding.searchProgressBar.setGone()
            }
        })

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
            when (it.status) {
                LoadingState.Status.RUNNING -> binding.franchisesProgressBar.setVisible()
                LoadingState.Status.SUCCESS -> binding.franchisesProgressBar.setGone()
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