package com.lukakordzaia.streamflow.ui.phone.home.toplistfragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.network.LoadingState
import com.lukakordzaia.streamflow.ui.baseclasses.BaseFragment
import com.lukakordzaia.streamflow.ui.phone.categories.singlegenre.SingleCategoryAdapter
import com.lukakordzaia.streamflow.utils.*
import kotlinx.android.synthetic.main.phone_single_category_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class TopMoviesFragment : BaseFragment() {
    private val viewModel by viewModel<SingleTopListViewModel>()
    private lateinit var singleCategoryAdapter: SingleCategoryAdapter
    private var page = 1
    private var pastVisibleItems: Int = 0
    private var visibleItemCount: Int = 0
    private var totalItemCount: Int = 0
    private var loading = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return getPersistentView(inflater, container, savedInstanceState, R.layout.phone_single_category_fragment)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!hasInitializedRootView) {
            Log.d("onviewcreated", "true")
            hasInitializedRootView = true
            viewModel.getTopMovies(page)
        }


        viewModel.noInternet.observe(viewLifecycleOwner, EventObserver {
            if (it) {
                requireContext().createToast(AppConstants.NO_INTERNET)
                Handler(Looper.getMainLooper()).postDelayed({
                    viewModel.getTopMovies(page)
                }, 3000)
            }
        })

        topBarListener("ტოპ ფილმები")

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
            singleCategoryAdapter.setCategoryTitleList(it)
        })

        rv_single_category.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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

        viewModel.navigateScreen.observe(viewLifecycleOwner, EventObserver {
            navController(it)
        })

        viewModel.toastMessage.observe(viewLifecycleOwner, EventObserver {
            requireContext().createToast(it)
        })
    }

    private fun fetchMoreTopMovies() {
        single_category_progressBar.setVisible()
        page++
        Log.d("currentpage", page.toString())
        viewModel.getTopMovies(page)
    }
}