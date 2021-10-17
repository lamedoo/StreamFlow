package com.lukakordzaia.streamflow.ui.phone.home.toplistfragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.databinding.FragmentPhoneSingleCategoryBinding
import com.lukakordzaia.streamflow.network.LoadingState
import com.lukakordzaia.streamflow.ui.baseclasses.BaseFragmentVM
import com.lukakordzaia.streamflow.ui.phone.sharedadapters.SingleCategoryAdapter
import com.lukakordzaia.streamflow.utils.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class TopListFragment : BaseFragmentVM<FragmentPhoneSingleCategoryBinding, SingleTopListViewModel>() {
    override val viewModel by viewModel<SingleTopListViewModel>()
    private val args: TopListFragmentArgs by navArgs()
    private lateinit var singleCategoryAdapter: SingleCategoryAdapter
    private var page = 1
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
        newMoviesContainer()
    }

    private fun setTopBar(title: Int) {
        topBarListener(resources.getString(title), binding.toolbar)
    }

    private fun fragmentObservers() {
        viewModel.noInternet.observe(viewLifecycleOwner, EventObserver {
            if (it) {
                requireContext().createToast(AppConstants.NO_INTERNET)
                Handler(Looper.getMainLooper()).postDelayed({
                    viewModel.getNewMovies(page)
                }, 3000)
            }
        })
    }

    private fun newMoviesContainer() {
        viewModel.listLoader.observe(viewLifecycleOwner, {
            when (it.status) {
                LoadingState.Status.RUNNING -> binding.progressBar.setVisible()
                LoadingState.Status.SUCCESS -> {
                    binding.progressBar.setGone()
                    loading = false
                }
            }
        })

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
                        fetchMoreTopMovies()
                    }
                }
            }
        })
    }

    private fun fetchMoreTopMovies() {
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