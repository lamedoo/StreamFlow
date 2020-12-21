package com.lukakordzaia.imoviesapp.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.lukakordzaia.imoviesapp.R
import com.lukakordzaia.imoviesapp.utils.EventObserver
import com.lukakordzaia.imoviesapp.utils.navController
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : Fragment(R.layout.fragment_home) {
    private lateinit var viewModel: HomeViewModel
    private lateinit var homeAdapter: HomeMovieAdapter
    private lateinit var tvShowAdapter: HomeTvShowAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        //New Movies List
        val movieLayout = GridLayoutManager(requireActivity(), 1, GridLayoutManager.HORIZONTAL, false)
        homeAdapter = HomeMovieAdapter(requireContext()) {
            viewModel.onSingleTitlePressed(it)
        }
        rv_main_top_movies.adapter =  homeAdapter
        rv_main_top_movies.layoutManager = movieLayout

        viewModel.getTopMovies()

        viewModel.movieList.observe(viewLifecycleOwner, Observer {
            homeAdapter.setMoviesList(it)
        })


        //New TvShows List
        val tvShowLayout = GridLayoutManager(requireActivity(), 1, GridLayoutManager.HORIZONTAL, false)
        tvShowAdapter = HomeTvShowAdapter(requireContext()) {
            viewModel.onSingleTitlePressed(it)
        }
        rv_main_top_tvshows.adapter = tvShowAdapter
        rv_main_top_tvshows.layoutManager = tvShowLayout

        viewModel.getTopTvShows()

        viewModel.tvShowList.observe(viewLifecycleOwner, Observer {
            tvShowAdapter.setTvShowsList(it)
        })



        viewModel.navigateScreen.observe(viewLifecycleOwner, EventObserver {
            navController(it)
        })
    }
}