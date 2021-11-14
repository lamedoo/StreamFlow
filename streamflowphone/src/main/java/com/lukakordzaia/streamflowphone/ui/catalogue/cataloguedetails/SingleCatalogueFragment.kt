package com.lukakordzaia.streamflowphone.ui.catalogue.cataloguedetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lukakordzaia.core.network.LoadingState
import com.lukakordzaia.core.utils.setVisible
import com.lukakordzaia.core.utils.setVisibleOrGone
import com.lukakordzaia.streamflowphone.databinding.FragmentPhoneSingleCategoryBinding
import com.lukakordzaia.streamflowphone.ui.baseclasses.BaseFragmentPhoneVM
import com.lukakordzaia.streamflowphone.ui.sharedadapters.SingleCategoryAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class SingleCatalogueFragment : BaseFragmentPhoneVM<FragmentPhoneSingleCategoryBinding, SingleCatalogueViewModel>() {
    private val args: SingleCatalogueFragmentArgs by navArgs()
    private var page = 1

    override val viewModel by viewModel<SingleCatalogueViewModel>()
    override val reload: () -> Unit = { viewModel.fetchContent(args.catalogueType, args.catalogueId, page) }

    private lateinit var singleCategoryAdapter: SingleCategoryAdapter
    private var pastVisibleItems: Int = 0
    private var visibleItemCount: Int = 0
    private var totalItemCount: Int = 0
    private var loading = false

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentPhoneSingleCategoryBinding
        get() = FragmentPhoneSingleCategoryBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.fetchContent(args.catalogueType, args.catalogueId, page)

        topBarListener(args.catalogueName, binding.toolbar)

        fragmentObservers()
        studiosContainer()
    }

    private fun fragmentObservers() {
        viewModel.generalLoader.observe(viewLifecycleOwner, {
            binding.progressBar.setVisibleOrGone(it == LoadingState.LOADING)
        })

        viewModel.singleCatalogueList.observe(viewLifecycleOwner, {
            singleCategoryAdapter.setItems(it)
        })
    }

    private fun studiosContainer() {
        val studiosLayout = GridLayoutManager(requireActivity(), 2, GridLayoutManager.VERTICAL, false)
        singleCategoryAdapter = SingleCategoryAdapter(requireContext()) {
            viewModel.onSingleTitlePressed(it)
        }
        binding.rvSingleCategory.apply {
            adapter = singleCategoryAdapter
            layoutManager = studiosLayout
        }

        viewModel.hasMorePage.observe(viewLifecycleOwner, {
            if (it) {
                binding.rvSingleCategory.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        if (dy > 0) {
                            visibleItemCount = studiosLayout.childCount
                            totalItemCount = studiosLayout.itemCount
                            pastVisibleItems = studiosLayout.findFirstVisibleItemPosition()

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
        viewModel.fetchContent(args.catalogueType, args.catalogueId, page)
        loading = false
    }
}