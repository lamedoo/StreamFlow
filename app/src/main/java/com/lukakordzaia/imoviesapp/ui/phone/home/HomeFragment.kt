package com.lukakordzaia.imoviesapp.ui.phone.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.lukakordzaia.imoviesapp.R
import com.lukakordzaia.imoviesapp.utils.EventObserver
import com.lukakordzaia.imoviesapp.utils.navController
import com.lukakordzaia.imoviesapp.utils.setGone
import com.lukakordzaia.imoviesapp.utils.setVisible
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : Fragment(R.layout.fragment_home) {
    private lateinit var viewModel: HomeViewModel
    private lateinit var homeWatchedAdapter: HomeWatchedAdapter
    private lateinit var homeMovieAdapter: HomeMovieAdapter
    private lateinit var homeTvShowAdapter: HomeTvShowAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        viewModel.isLoading.observe(viewLifecycleOwner, EventObserver {
            if (!it) {
                home_progressBar.setGone()
                home_main_container.setVisible()
            }
        })

        //Watched Titles List
        val watchedLayout = GridLayoutManager(requireActivity(), 1, GridLayoutManager.HORIZONTAL, false)
        homeWatchedAdapter = HomeWatchedAdapter(requireContext()) {
            viewModel.onWatchedTitlePressed(it)
        }
        rv_main_watched_titles.adapter = homeWatchedAdapter
        rv_main_watched_titles.layoutManager = watchedLayout

        viewModel.getWatchedFromDb(requireContext()).observe(viewLifecycleOwner, {
            if (!it.isNullOrEmpty()) {
                main_watched_titles_none.setGone()
                viewModel.getWatchedTitles(it)
            }
        })

        viewModel.watchedList.observe(viewLifecycleOwner, {
            homeWatchedAdapter.setWatchedTitlesList(it)
        })

        //New Movies List
        val movieLayout = GridLayoutManager(requireActivity(), 1, GridLayoutManager.HORIZONTAL, false)
        homeMovieAdapter = HomeMovieAdapter(requireContext()) {
            viewModel.onSingleTitlePressed(it)
        }
        rv_main_top_movies.adapter =  homeMovieAdapter
        rv_main_top_movies.layoutManager = movieLayout

        viewModel.getTopMovies()

        viewModel.movieList.observe(viewLifecycleOwner, Observer {
            homeMovieAdapter.setMoviesList(it)
        })


        //New TvShows List
        val tvShowLayout = GridLayoutManager(requireActivity(), 1, GridLayoutManager.HORIZONTAL, false)
        homeTvShowAdapter = HomeTvShowAdapter(requireContext()) {
            viewModel.onSingleTitlePressed(it)
        }
        rv_main_top_tvshows.adapter = homeTvShowAdapter
        rv_main_top_tvshows.layoutManager = tvShowLayout

        viewModel.getTopTvShows()

        viewModel.tvShowList.observe(viewLifecycleOwner, Observer {
            homeTvShowAdapter.setTvShowsList(it)
        })

        viewModel.navigateScreen.observe(viewLifecycleOwner, EventObserver {
            navController(it)
        })
    }
}