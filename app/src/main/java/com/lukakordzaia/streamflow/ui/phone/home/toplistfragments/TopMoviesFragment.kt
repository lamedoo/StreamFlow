package com.lukakordzaia.streamflow.ui.phone.home.toplistfragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.network.LoadingState
import com.lukakordzaia.streamflow.ui.phone.categories.singlegenre.SingleCategoryAdapter
import com.lukakordzaia.streamflow.ui.phone.home.HomeViewModel
import com.lukakordzaia.streamflow.utils.*
import kotlinx.android.synthetic.main.phone_single_category_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class TopMoviesFragment : Fragment(R.layout.phone_single_category_fragment) {
    private val viewModel by viewModel<HomeViewModel>()
    private lateinit var singleCategoryAdapter: SingleCategoryAdapter
    private var page = 1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getTopMovies(page)

        val layoutManager = GridLayoutManager(requireActivity(), 2, GridLayoutManager.VERTICAL, false)

        viewModel.topMovieLoader.observe(viewLifecycleOwner, {
            when (it.status) {
                LoadingState.Status.RUNNING -> single_category_progressBar.setVisible()
                LoadingState.Status.SUCCESS -> single_category_progressBar.setGone()
            }
        })

        singleCategoryAdapter = SingleCategoryAdapter(requireContext()) {
            viewModel.onSingleTitlePressed(AppConstants.NAV_TOP_MOVIES_TO_SINGLE, it)
        }
        rv_single_category.adapter = singleCategoryAdapter
        rv_single_category.layoutManager = layoutManager

        viewModel.topMovieList.observe(viewLifecycleOwner, {
            singleCategoryAdapter.setGenreTitleList(it)
        })

        infiniteScroll(single_category_nested_scroll) { fetchMoreTopMovies() }

        viewModel.navigateScreen.observe(viewLifecycleOwner, EventObserver {
            navController(it)
        })
    }

    private fun fetchMoreTopMovies() {
        single_category_progressBar.setVisible()
        page++
        Log.d("currentpage", page.toString())
        viewModel.getTopMovies(page)
    }
}