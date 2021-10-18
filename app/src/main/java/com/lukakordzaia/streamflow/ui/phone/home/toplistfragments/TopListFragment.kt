package com.lukakordzaia.streamflow.ui.phone.home.toplistfragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.databinding.FragmentPhoneSingleCategoryBinding
import com.lukakordzaia.streamflow.network.LoadingState
import com.lukakordzaia.streamflow.ui.baseclasses.BaseFragmentPhoneVM
import com.lukakordzaia.streamflow.ui.phone.sharedadapters.SingleCategoryAdapter
import com.lukakordzaia.streamflow.utils.AppConstants
import com.lukakordzaia.streamflow.utils.setGone
import com.lukakordzaia.streamflow.utils.setVisible
import org.koin.androidx.viewmodel.ext.android.viewModel

class TopListFragment : BaseFragmentPhoneVM<FragmentPhoneSingleCategoryBinding, TopListViewModel>() {
    private var page = 1
    private val args: TopListFragmentArgs by navArgs()

    override val viewModel by viewModel<TopListViewModel>()
    override val reload: () -> Unit = {
        when (args.type) {
            AppConstants.LIST_NEW_MOVIES -> {
                viewModel.getNewMovies(page)
                setTopBar(R.string.new_movies)
            }
            AppConstants.LIST_TOP_MOVIES -> {
                viewModel.getTopMovies(page)
                setTopBar(R.string.top_movies)
            }
            AppConstants.LIST_TOP_TV_SHOWS -> {
                viewModel.getTopTvShows(page)
                setTopBar(R.string.top_tv_shows)
            }
        }
    }

    private lateinit var singleCategoryAdapter: SingleCategoryAdapter
    private var pastVisibleItems: Int = 0
    private var visibleItemCount: Int = 0
    private var totalItemCount: Int = 0
    private var loading = false

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentPhoneSingleCategoryBinding
        get() = FragmentPhoneSingleCategoryBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        when (args.type) {
            AppConstants.LIST_NEW_MOVIES -> {
                viewModel.getNewMovies(page)
                setTopBar(R.string.new_movies)
            }
            AppConstants.LIST_TOP_MOVIES -> {
                viewModel.getTopMovies(page)
                setTopBar(R.string.top_movies)
            }
            AppConstants.LIST_TOP_TV_SHOWS -> {
                viewModel.getTopTvShows(page)
                setTopBar(R.string.top_tv_shows)
            }
        }

        fragmentObservers()
        topListContainer()
    }

    private fun setTopBar(title: Int) {
        topBarListener(resources.getString(title), binding.toolbar)
    }

    private fun fragmentObservers() {
        viewModel.generalLoader.observe(viewLifecycleOwner, {
            when (it) {
                LoadingState.LOADING -> binding.progressBar.setVisible()
                LoadingState.LOADED -> {
                    binding.progressBar.setGone()
                    loading = false
                }
            }
        })
    }

    private fun topListContainer() {
        val layoutManager = GridLayoutManager(requireActivity(), 2, GridLayoutManager.VERTICAL, false)
        singleCategoryAdapter = SingleCategoryAdapter(requireContext()) {
            viewModel.onSingleTitlePressed(it)
        }
        binding.rvSingleCategory.adapter = singleCategoryAdapter
        binding.rvSingleCategory.layoutManager = layoutManager

        viewModel.list.observe(viewLifecycleOwner, {
            singleCategoryAdapter.setItems(it)
        })

        binding.rvSingleCategory.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    visibleItemCount = layoutManager.childCount
                    totalItemCount = layoutManager.itemCount
                    pastVisibleItems = layoutManager.findFirstVisibleItemPosition()

                    if (!loading && (visibleItemCount + pastVisibleItems) >= totalItemCount) {
                        loading = true
                        fetchMoreTitles()
                    }
                }
            }
        })
    }

    private fun fetchMoreTitles() {
        binding.progressBar.setVisible()
        page++
        when (args.type) {
            AppConstants.LIST_NEW_MOVIES -> {
                viewModel.getNewMovies(page)
            }
            AppConstants.LIST_TOP_MOVIES -> {
                viewModel.getTopMovies(page)
            }
            AppConstants.LIST_TOP_TV_SHOWS -> {
                viewModel.getTopTvShows(page)
            }
        }
        loading = false
    }
}