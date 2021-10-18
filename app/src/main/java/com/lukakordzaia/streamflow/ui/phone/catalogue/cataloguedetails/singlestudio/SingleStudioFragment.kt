package com.lukakordzaia.streamflow.ui.phone.catalogue.cataloguedetails.singlestudio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lukakordzaia.streamflow.databinding.FragmentPhoneSingleCategoryBinding
import com.lukakordzaia.streamflow.network.LoadingState
import com.lukakordzaia.streamflow.ui.baseclasses.BaseFragmentPhoneVM
import com.lukakordzaia.streamflow.ui.phone.catalogue.cataloguedetails.SingleCategoryViewModel
import com.lukakordzaia.streamflow.ui.phone.sharedadapters.SingleCategoryAdapter
import com.lukakordzaia.streamflow.utils.AppConstants
import com.lukakordzaia.streamflow.utils.setGone
import com.lukakordzaia.streamflow.utils.setVisible
import org.koin.androidx.viewmodel.ext.android.viewModel

class SingleStudioFragment : BaseFragmentPhoneVM<FragmentPhoneSingleCategoryBinding, SingleCategoryViewModel>() {
    override val viewModel by viewModel<SingleCategoryViewModel>()
    override val reload: () -> Unit = { viewModel.getSingleStudio(args.studioId, page) }

    private lateinit var singleCategoryAdapter: SingleCategoryAdapter
    private val args: SingleStudioFragmentArgs by navArgs()
    private var page = 1
    private var pastVisibleItems: Int = 0
    private var visibleItemCount: Int = 0
    private var totalItemCount: Int = 0
    private var loading = false

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentPhoneSingleCategoryBinding
        get() = FragmentPhoneSingleCategoryBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getSingleStudio(args.studioId, page)

        topBarListener(args.studioName, binding.toolbar)

        fragmentObservers()
        studiosContainer()
    }

    private fun fragmentObservers() {
        viewModel.generalLoader.observe(viewLifecycleOwner, {
            when (it) {
                LoadingState.LOADING -> binding.progressBar.setVisible()
                LoadingState.LOADED -> binding.progressBar.setGone()
            }
        })
    }

    private fun studiosContainer() {
        val layoutManager = GridLayoutManager(requireActivity(), 2, GridLayoutManager.VERTICAL, false)
        singleCategoryAdapter = SingleCategoryAdapter(requireContext()) {
            viewModel.onSingleTitlePressed(it, AppConstants.NAV_STUDIO_TO_SINGLE)
        }
        binding.rvSingleCategory.adapter = singleCategoryAdapter
        binding.rvSingleCategory.layoutManager = layoutManager

        viewModel.singleStudioList.observe(viewLifecycleOwner, {
            singleCategoryAdapter.setItems(it)
        })

        viewModel.hasMorePage.observe(viewLifecycleOwner, {
            if (it) {
                binding.rvSingleCategory.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        if (dy > 0) {
                            visibleItemCount = layoutManager.childCount
                            totalItemCount = layoutManager.itemCount
                            pastVisibleItems = layoutManager.findFirstVisibleItemPosition()

                            if (!loading && (visibleItemCount + pastVisibleItems) >= totalItemCount) {
                                loading = true
                                fetchMoreTitle()
                            }
                        }
                    }
                })
            }
        })
    }


    private fun fetchMoreTitle() {
        binding.progressBar.setVisible()
        page++
        viewModel.getSingleStudio(args.studioId, page)
        loading = false
    }
}