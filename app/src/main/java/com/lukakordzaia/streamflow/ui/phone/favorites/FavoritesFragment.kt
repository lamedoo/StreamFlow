package com.lukakordzaia.streamflow.ui.phone.favorites

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.network.LoadingState
import com.lukakordzaia.streamflow.ui.baseclasses.BaseFragment
import com.lukakordzaia.streamflow.utils.EventObserver
import com.lukakordzaia.streamflow.utils.navController
import com.lukakordzaia.streamflow.utils.setGone
import com.lukakordzaia.streamflow.utils.setVisible
import kotlinx.android.synthetic.main.phone_favorites_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesFragment : BaseFragment(R.layout.phone_favorites_fragment) {
    private val favoritesViewModel: FavoritesViewModel by viewModel()
    private lateinit var favoritesMoviesAdapter: FavoritesAdapter
    private lateinit var favoriteTvShowsAdapter: FavoritesAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!authSharedPreferences.getAccessToken().isNullOrBlank()) {
            favoritesViewModel.getSfListByMovies("Bearer ${authSharedPreferences.getAccessToken()}")
            favoritesViewModel.getSfListByTvShows("Bearer ${authSharedPreferences.getAccessToken()}")
        } else {
            favorite_movies_title.setGone()
            favorite_tvshows_title.setGone()
            favorite_movies_progressBar.setGone()
            favorite_tvshows_progressBar.setGone()
        }

        // Favorite Movies
        favoritesViewModel.favoriteMoviesLoader.observe(viewLifecycleOwner, {
            when (it.status) {
                LoadingState.Status.RUNNING -> favorite_movies_progressBar.setVisible()
                LoadingState.Status.SUCCESS -> favorite_movies_progressBar.setGone()
            }
        })

        val moviesLayout = GridLayoutManager(requireActivity(), 1, GridLayoutManager.HORIZONTAL, false)
        favoritesMoviesAdapter = FavoritesAdapter(requireContext()) {
            favoritesViewModel.onSingleTitlePressed(it)
        }
        rv_favorites_movies.layoutManager = moviesLayout
        rv_favorites_movies.adapter = favoritesMoviesAdapter


        favoritesViewModel.movieSearchResult.observe(viewLifecycleOwner, {
            favoritesMoviesAdapter.setFavoritesTitleList(it)
        })


        // Favorite TV Shows
        favoritesViewModel.favoriteTvShowsLoader.observe(viewLifecycleOwner, {
            when (it.status) {
                LoadingState.Status.RUNNING -> favorite_tvshows_progressBar.setVisible()
                LoadingState.Status.SUCCESS -> favorite_tvshows_progressBar.setGone()
            }
        })

        val tvShowsLayout = GridLayoutManager(requireActivity(), 1, GridLayoutManager.HORIZONTAL, false)
        favoriteTvShowsAdapter = FavoritesAdapter(requireContext()) {
            favoritesViewModel.onSingleTitlePressed(it)
        }
        rv_favorites_tvshows.layoutManager = tvShowsLayout
        rv_favorites_tvshows.adapter = favoriteTvShowsAdapter

        favoritesViewModel.tvShowSearchResult.observe(viewLifecycleOwner, {
            favoriteTvShowsAdapter.setFavoritesTitleList(it)
        })



        favoritesViewModel.navigateScreen.observe(viewLifecycleOwner, EventObserver{
            navController(it)
        })
    }
}