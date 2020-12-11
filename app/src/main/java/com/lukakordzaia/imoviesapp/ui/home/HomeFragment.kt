package com.lukakordzaia.imoviesapp.ui.home

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.lukakordzaia.imoviesapp.R
import com.lukakordzaia.imoviesapp.utils.EventObserver
import com.lukakordzaia.imoviesapp.utils.navController
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment(R.layout.fragment_home) {
    private lateinit var viewModel: HomeViewModel
    private lateinit var adapter: HomeAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        adapter = HomeAdapter(requireContext()) {
            viewModel.onSingleMoviePressed(it)
        }
        rv_movies.adapter = adapter

        viewModel.getMovies()

        viewModel.movieList.observe(viewLifecycleOwner, Observer {
            Log.d("moviedata", "$it")
            adapter.setMoviesList(it)
        })

        viewModel.navigateScreen.observe(viewLifecycleOwner, EventObserver {
            navController(it)
        })
    }
}