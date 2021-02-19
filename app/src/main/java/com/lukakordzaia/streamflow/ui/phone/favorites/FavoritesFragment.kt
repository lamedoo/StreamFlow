package com.lukakordzaia.streamflow.ui.phone.favorites

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.ui.baseclasses.BaseFragment
import com.lukakordzaia.streamflow.utils.EventObserver
import com.lukakordzaia.streamflow.utils.navController
import kotlinx.android.synthetic.main.phone_favorites_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesFragment : BaseFragment(R.layout.phone_favorites_fragment) {
    private val favoritesViewModel: FavoritesViewModel by viewModel()
    private lateinit var favoritesMoviesAdapter: FavoritesAdapter
    private lateinit var favoriteTvShowsAdapter: FavoritesAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Favorite Movies
        favoritesViewModel.getSfListByMovies("Bearer ${authSharedPreferences.getAccessToken()}")

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
        favoritesViewModel.getSfListByTvShows("Bearer ${authSharedPreferences.getAccessToken()}")

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