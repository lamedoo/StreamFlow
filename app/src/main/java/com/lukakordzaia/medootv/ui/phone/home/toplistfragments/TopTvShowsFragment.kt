package com.lukakordzaia.medootv.ui.phone.home.toplistfragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.lukakordzaia.medootv.R
import com.lukakordzaia.medootv.network.LoadingState
import com.lukakordzaia.medootv.ui.phone.categories.singlegenre.SingleCategoryAdapter
import com.lukakordzaia.medootv.ui.phone.home.HomeViewModel
import com.lukakordzaia.medootv.utils.*
import kotlinx.android.synthetic.main.phone_single_category_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class TopTvShowsFragment : Fragment(R.layout.phone_single_category_fragment) {
    private val viewModel by viewModel<HomeViewModel>()
    private lateinit var singleCategoryAdapter: SingleCategoryAdapter
    private var page = 1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getTopTvShows(page)

        val layoutManager = GridLayoutManager(requireActivity(), 2, GridLayoutManager.VERTICAL, false)

        viewModel.topTvShowsLoader.observe(viewLifecycleOwner, {
            when (it.status) {
                LoadingState.Status.RUNNING -> single_category_progressBar.setVisible()
                LoadingState.Status.SUCCESS -> single_category_progressBar.setGone()
            }
        })

        singleCategoryAdapter = SingleCategoryAdapter(requireContext()) {
            viewModel.onSingleTitlePressed(AppConstants.NAV_TOP_TV_SHOWS_TO_SINGLE, it)
        }
        rv_single_category.adapter = singleCategoryAdapter
        rv_single_category.layoutManager = layoutManager

        viewModel.topTvShowList.observe(viewLifecycleOwner, {
            singleCategoryAdapter.setGenreTitleList(it)
        })

        infiniteScroll(single_category_nested_scroll) { fetchMoreTopTvShows() }

        viewModel.navigateScreen.observe(viewLifecycleOwner, EventObserver {
            navController(it)
        })
    }

    private fun fetchMoreTopTvShows() {
        single_category_progressBar.setVisible()
        page++
        Log.d("currentpage", page.toString())
        viewModel.getTopTvShows(page)
    }
}