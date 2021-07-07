package com.lukakordzaia.streamflow.ui.phone.categories.singlestudio

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.databinding.FragmentPhoneSingleCategoryBinding
import com.lukakordzaia.streamflow.network.LoadingState
import com.lukakordzaia.streamflow.ui.baseclasses.BaseFragment
import com.lukakordzaia.streamflow.ui.phone.categories.singlegenre.SingleCategoryAdapter
import com.lukakordzaia.streamflow.ui.phone.categories.singlegenre.SingleCategoryViewModel
import com.lukakordzaia.streamflow.utils.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class SingleStudioFragment : BaseFragment<FragmentPhoneSingleCategoryBinding>() {
    private val singleCategoryViewModel: SingleCategoryViewModel by viewModel()
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

        if (!hasInitializedRootView) {
            hasInitializedRootView = true
            singleCategoryViewModel.getSingleStudio(args.studioId, page)
        }

        topBarListener(args.studioName)

        fragmentObservers()
        studiosContainer()
    }

    private fun fragmentObservers() {
        singleCategoryViewModel.noInternet.observe(viewLifecycleOwner, EventObserver {
            if (it) {
                requireContext().createToast(AppConstants.NO_INTERNET)
                Handler(Looper.getMainLooper()).postDelayed({
                    singleCategoryViewModel.getSingleStudio(args.studioId, page)
                }, 5000)
            }
        })

        singleCategoryViewModel.navigateScreen.observe(viewLifecycleOwner, EventObserver {
            navController(it)
        })
    }

    private fun studiosContainer() {
        singleCategoryViewModel.categoryLoader.observe(viewLifecycleOwner, {
            when (it.status) {
                LoadingState.Status.RUNNING -> binding.progressBar.setVisible()
                LoadingState.Status.SUCCESS -> binding.progressBar.setGone()
            }
        })

        val layoutManager = GridLayoutManager(requireActivity(), 2, GridLayoutManager.VERTICAL, false)
        singleCategoryAdapter = SingleCategoryAdapter(requireContext()) {
            singleCategoryViewModel.onSingleTitlePressed(it, AppConstants.NAV_STUDIO_TO_SINGLE)
        }
        binding.rvSingleCategory.adapter = singleCategoryAdapter
        binding.rvSingleCategory.layoutManager = layoutManager

        singleCategoryViewModel.singleStudioList.observe(viewLifecycleOwner, {
            singleCategoryAdapter.setCategoryTitleList(it)
        })

        singleCategoryViewModel.hasMorePage.observe(viewLifecycleOwner, {
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
        Log.d("currentpage", page.toString())
        singleCategoryViewModel.getSingleStudio(args.studioId, page)
    }
}