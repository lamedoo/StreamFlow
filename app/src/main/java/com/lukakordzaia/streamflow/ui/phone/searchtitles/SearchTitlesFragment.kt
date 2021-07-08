package com.lukakordzaia.streamflow.ui.phone.searchtitles

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.databinding.FragmentPhoneSearchTitlesBinding
import com.lukakordzaia.streamflow.network.LoadingState
import com.lukakordzaia.streamflow.ui.baseclasses.BaseFragment
import com.lukakordzaia.streamflow.ui.customviews.SearchEditText
import com.lukakordzaia.streamflow.utils.*
import com.xiaofeng.flowlayoutmanager.FlowLayoutManager
import kotlinx.android.synthetic.main.main_top_toolbar.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchTitlesFragment : BaseFragment<FragmentPhoneSearchTitlesBinding>() {
    private val searchTitlesViewModel: SearchTitlesViewModel by viewModel()
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

        home_favorites.setGone()
        home_profile.setGone()

        fragmentObservers()
        searchInput()
        searchTitlesContainer()
        franchisesContainer()
    }

    private fun fragmentObservers() {
        searchTitlesViewModel.noInternet.observe(viewLifecycleOwner, EventObserver {
            if (it) {
                requireContext().createToast(AppConstants.NO_INTERNET)
                Handler(Looper.getMainLooper()).postDelayed({
                    searchTitlesViewModel.refreshContent()
                }, 5000)
            }
        })

        searchTitlesViewModel.navigateScreen.observe(viewLifecycleOwner, EventObserver {
            navController(it)
        })

        searchTitlesViewModel.toastMessage.observe(viewLifecycleOwner, EventObserver {
            requireContext().createToast(it)
        })
    }

    private fun searchInput() {
        binding.searchTitleText.setQueryTextChangeListener(object : SearchEditText.QueryTextListener {
            override fun onQueryTextSubmit(query: String?) {
//                if (!query.isNullOrBlank()) {
//                    binding.rvSearchTitlesContainer.setVisible()
//                    binding.topSearchContainer.setGone()
//                } else {
//                    searchTitlesViewModel.clearSearchResults()
//                    binding.searchTitleText.setText("")
//                    binding.rvSearchTitlesContainer.setGone()
//                    binding.topSearchContainer.setVisible()
//                }
            }


            override fun onQueryTextChange(newText: String?) {
                if (newText.isNullOrBlank()) {
                    binding.rvSearchTitlesContainer.setGone()
                    binding.topSearchContainer.setVisible()
                    searchTitlesViewModel.clearSearchResults()
                } else {
                    searchTitlesViewModel.clearSearchResults()
                    searchTitlesViewModel.getSearchTitles(newText, page)
                    binding.rvSearchTitlesContainer.setVisible()
                    binding.topSearchContainer.setGone()
                }
            }
        })
    }

    private fun searchTitlesContainer() {
        searchTitlesViewModel.searchLoader.observe(viewLifecycleOwner, {
            when (it.status) {
                LoadingState.Status.RUNNING -> binding.searchProgressBar.setVisible()
                LoadingState.Status.SUCCESS -> binding.searchProgressBar.setGone()
            }
        })

        val layoutManager = LinearLayoutManager(requireActivity(), GridLayoutManager.VERTICAL, false)
        searchTitlesAdapter = SearchTitlesAdapter(requireContext()) {
            searchTitlesViewModel.onSingleTitlePressed(it)
        }

        binding.rvSearchTitles.adapter = searchTitlesAdapter
        binding.rvSearchTitles.layoutManager = layoutManager

        searchTitlesViewModel.searchList.observe(viewLifecycleOwner, {
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
        searchTitlesViewModel.getTopFranchises()

        topFranchisesAdapter = TopFranchisesAdapter(requireContext()) { titleName, position ->
            onFranchiseAnimationEnd(titleName)
        }
        binding.rvTopFranchises.layoutManager = FlowLayoutManager().apply {
            isAutoMeasureEnabled = true
        }
        binding.rvTopFranchises.adapter = topFranchisesAdapter

        searchTitlesViewModel.franchisesLoader.observe(viewLifecycleOwner, {
            when (it.status) {
                LoadingState.Status.RUNNING -> binding.franchisesProgressBar.setVisible()
                LoadingState.Status.SUCCESS -> binding.franchisesProgressBar.setGone()
            }
        })

        searchTitlesViewModel.franchiseList.observe(viewLifecycleOwner, {
            topFranchisesAdapter.setFranchisesList(it)
        })
    }

    private fun fetchMoreResults() {
        page++
        searchTitlesViewModel.getSearchTitles(binding.searchTitleText.text.toString(), page)
        binding.searchProgressBar.setVisible()
        loading = false
    }

    private fun onFranchiseAnimationEnd(titleName: String) {
        (binding.searchTitleText as TextView).text = titleName
        searchTitlesViewModel.getSearchTitles(titleName, 1)
        binding.rvSearchTitlesContainer.setVisible()
        binding.topSearchContainer.setGone()
    }
}