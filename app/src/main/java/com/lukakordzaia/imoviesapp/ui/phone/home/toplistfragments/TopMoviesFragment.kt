package com.lukakordzaia.imoviesapp.ui.phone.home.toplistfragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.lukakordzaia.imoviesapp.R
import com.lukakordzaia.imoviesapp.network.LoadingState
import com.lukakordzaia.imoviesapp.ui.phone.genres.singlegenre.SingleGenreAdapter
import com.lukakordzaia.imoviesapp.ui.phone.home.HomeViewModel
import com.lukakordzaia.imoviesapp.utils.*
import kotlinx.android.synthetic.main.phone_home_framgent.*
import kotlinx.android.synthetic.main.phone_single_genre_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class TopMoviesFragment : Fragment(R.layout.phone_single_genre_fragment) {
    private val viewModel by viewModel<HomeViewModel>()
    private lateinit var singleGenreAdapter: SingleGenreAdapter
    private var page = 1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getTopMovies(page)

        val layoutManager = GridLayoutManager(requireActivity(), 2, GridLayoutManager.VERTICAL, false)

        viewModel.topMovieLoader.observe(viewLifecycleOwner, {
            when (it.status) {
                LoadingState.Status.RUNNING -> single_genre_progressBar.setVisible()
                LoadingState.Status.SUCCESS -> single_genre_progressBar.setGone()
            }
        })

        singleGenreAdapter = SingleGenreAdapter(requireContext()) {
            viewModel.onSingleTitlePressed(AppConstants.NAV_TOP_MOVIES_TO_SINGLE, it)
        }
        rv_single_genre.adapter = singleGenreAdapter
        rv_single_genre.layoutManager = layoutManager

        viewModel.topMovieList.observe(viewLifecycleOwner, {
            singleGenreAdapter.setGenreTitleList(it)
        })

        infiniteScroll(singlegenre_nested_scroll) { fetchMoreTopMovies() }

        viewModel.navigateScreen.observe(viewLifecycleOwner, EventObserver {
            navController(it)
        })
    }

    private fun fetchMoreTopMovies() {
        single_genre_progressBar.setVisible()
        page++
        Log.d("currentpage", page.toString())
        viewModel.getTopMovies(page)
    }
}