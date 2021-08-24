package com.lukakordzaia.streamflow.ui.phone.phonewatchlist

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.lukakordzaia.streamflow.databinding.DialogRemoveFavoriteBinding
import com.lukakordzaia.streamflow.databinding.FragmentPhoneWatchlistBinding
import com.lukakordzaia.streamflow.datamodels.SingleTitleModel
import com.lukakordzaia.streamflow.network.LoadingState
import com.lukakordzaia.streamflow.ui.baseclasses.BaseFragment
import com.lukakordzaia.streamflow.utils.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class PhoneWatchlistFragment : BaseFragment<FragmentPhoneWatchlistBinding>() {
    private val phoneWatchlistViewModel: PhoneWatchlistViewModel by viewModel()
    private lateinit var watchlistMoviesAdapter: WatchlistAdapter
    private lateinit var watchlistTvShowsAdapter: WatchlistAdapter

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentPhoneWatchlistBinding
        get() = FragmentPhoneWatchlistBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authCheck()
        fragmentListeners()
        fragmentObservers()
        favMoviesContainer()
        favTvShowsContainer()
    }

    private fun authCheck() {
        if (authSharedPreferences.getLoginToken() != "") {
            phoneWatchlistViewModel.getUserWatchlist()
            binding.favoriteMoviesContainer.setVisible()
            binding.favoriteTvshowsContainer.setVisible()
            binding.favoriteNoAuth.setGone()
        } else {
            binding.favoriteMoviesContainer.setGone()
            binding.favoriteTvshowsContainer.setGone()
            binding.favoriteNoAuth.setVisible()
        }
    }

    private fun fragmentListeners() {
        binding.toolbar.homeProfile.setOnClickListener {
            phoneWatchlistViewModel.onProfileButtonPressed()
        }

        binding.profileButton.setOnClickListener {
            phoneWatchlistViewModel.onProfileButtonPressed()
        }
    }

    private fun fragmentObservers() {
        phoneWatchlistViewModel.userWatchlist.observe(viewLifecycleOwner, { watchlist ->
            val movies: MutableList<SingleTitleModel> = ArrayList()
            val tvShows: MutableList<SingleTitleModel> = ArrayList()

            watchlist.forEach {
                if (it.isTvShow) {
                    tvShows.add(it)
                } else {
                    movies.add(it)
                }
            }

            if (movies.isEmpty()) {
                phoneWatchlistViewModel.favoriteNoMovies.value = true
            } else {
                watchlistMoviesAdapter.setItems(movies)
            }

            if (tvShows.isEmpty()) {
                phoneWatchlistViewModel.favoriteNoTvShows.value = true
            } else {
                watchlistTvShowsAdapter.setItems(tvShows)
            }
        })

        phoneWatchlistViewModel.toastMessage.observe(viewLifecycleOwner, EventObserver {
            requireContext().createToast(it)
        })

        phoneWatchlistViewModel.navigateScreen.observe(viewLifecycleOwner, EventObserver {
            navController(it)
        })
    }

    private fun favMoviesContainer() {
        phoneWatchlistViewModel.favoriteMoviesLoader.observe(viewLifecycleOwner, {
            when (it.status) {
                LoadingState.Status.RUNNING -> {
                    binding.favoriteMoviesProgressBar.setVisible()
                    binding.rvFavoritesMovies.setGone()
                }
                LoadingState.Status.SUCCESS -> {
                    binding.favoriteMoviesProgressBar.setGone()
                    binding.rvFavoritesMovies.setVisible()
                }
            }
        })

        phoneWatchlistViewModel.favoriteNoMovies.observe(viewLifecycleOwner, {
            if (it) {
                binding.favoriteNoMovies.setVisible()
            }
        })

        val moviesLayout = GridLayoutManager(requireActivity(), 1, GridLayoutManager.HORIZONTAL, false)
        watchlistMoviesAdapter = WatchlistAdapter(requireContext(),
            {
                phoneWatchlistViewModel.onSingleTitlePressed(it)
            },
            { titleId: Int ->
                removeTitleDialog(titleId)
            }
        )
        binding.rvFavoritesMovies.layoutManager = moviesLayout
        binding.rvFavoritesMovies.adapter = watchlistMoviesAdapter
    }

    private fun favTvShowsContainer() {
        phoneWatchlistViewModel.favoriteTvShowsLoader.observe(viewLifecycleOwner, {
            when (it.status) {
                LoadingState.Status.RUNNING -> {
                    binding.favoriteTvshowsProgressBar.setVisible()
                    binding.rvFavoritesTvshows.setGone()
                }
                LoadingState.Status.SUCCESS -> {
                    binding.favoriteTvshowsProgressBar.setGone()
                    binding.rvFavoritesTvshows.setVisible()
                }
            }
        })

        phoneWatchlistViewModel.favoriteNoTvShows.observe(viewLifecycleOwner, {
            if (it) {
                binding.favoriteNoTvshows.setVisible()
            }
        })

        val tvShowsLayout = GridLayoutManager(requireActivity(), 1, GridLayoutManager.HORIZONTAL, false)
        watchlistTvShowsAdapter = WatchlistAdapter(requireContext(),
            {
                phoneWatchlistViewModel.onSingleTitlePressed(it)
            },
            { titleId: Int ->
                removeTitleDialog(titleId)
            }
        )
        binding.rvFavoritesTvshows.layoutManager = tvShowsLayout
        binding.rvFavoritesTvshows.adapter = watchlistTvShowsAdapter
    }

    private fun removeTitleDialog(titleId: Int) {
        val binding = DialogRemoveFavoriteBinding.inflate(LayoutInflater.from(requireContext()))
        val removeFavorite = Dialog(requireContext())
        removeFavorite.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        removeFavorite.setContentView(binding.root)

        binding.confirmButton.setOnClickListener {
            phoneWatchlistViewModel.deleteWatchlistTitle(titleId)
            removeFavorite.dismiss()
        }
        binding.cancelButton.setOnClickListener {
            removeFavorite.dismiss()
        }
        removeFavorite.show()
    }
}