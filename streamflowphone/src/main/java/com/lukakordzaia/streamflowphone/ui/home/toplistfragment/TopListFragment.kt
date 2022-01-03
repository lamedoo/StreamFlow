package com.lukakordzaia.streamflowphone.ui.home.toplistfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lukakordzaia.core.utils.AppConstants
import com.lukakordzaia.core.network.LoadingState
import com.lukakordzaia.core.utils.setVisible
import com.lukakordzaia.core.utils.setVisibleOrGone
import com.lukakordzaia.streamflowphone.R
import com.lukakordzaia.streamflowphone.databinding.FragmentPhoneSingleCategoryBinding
import com.lukakordzaia.streamflowphone.ui.baseclasses.BaseFragmentPhoneVM
import com.lukakordzaia.streamflowphone.ui.sharedadapters.SingleCategoryAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class TopListFragment : BaseFragmentPhoneVM<FragmentPhoneSingleCategoryBinding, TopListViewModel>() {
    private var page = 1
    private val args: TopListFragmentArgs by navArgs()

    override val viewModel by viewModel<TopListViewModel>()
    override val reload: () -> Unit = { viewModel.fetchContent(args.type, page) }

    private lateinit var singleCategoryAdapter: SingleCategoryAdapter
    private var pastVisibleItems: Int = 0
    private var visibleItemCount: Int = 0
    private var totalItemCount: Int = 0
    private var loading = false

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentPhoneSingleCategoryBinding
        get() = FragmentPhoneSingleCategoryBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.fetchContent(args.type, page)

        setTopBar()
        fragmentObservers()
        topListContainer()
    }

    private fun setTopBar() {
        when (args.type) {
            AppConstants.LIST_NEW_MOVIES -> {
                topBarListener(resources.getString(R.string.new_movies), binding.toolbar)
            }
            AppConstants.LIST_TOP_MOVIES -> {
                topBarListener(resources.getString(R.string.top_movies), binding.toolbar)
            }
            AppConstants.LIST_TOP_TV_SHOWS -> {
                topBarListener(resources.getString(R.string.top_tv_shows), binding.toolbar)

            }
        }
    }

    private fun fragmentObservers() {
        viewModel.generalLoader.observe(viewLifecycleOwner, {
            binding.progressBar.setVisibleOrGone(it == LoadingState.LOADING)
            if (it == LoadingState.LOADED) {
                loading = false
            }
        })

        viewModel.list.observe(viewLifecycleOwner, {
            singleCategoryAdapter.setItems(it)
        })
    }

    private fun topListContainer() {
        val layoutManager = GridLayoutManager(requireActivity(), 2, GridLayoutManager.VERTICAL, false)
        singleCategoryAdapter = SingleCategoryAdapter {
            viewModel.onSingleTitlePressed(it)
        }
        binding.rvSingleCategory.adapter = singleCategoryAdapter
        binding.rvSingleCategory.layoutManager = layoutManager

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
        viewModel.fetchContent(args.type, page)
        loading = false
    }

    override fun onDestroyView() {
        binding.rvSingleCategory.adapter = null
        super.onDestroyView()
    }
}