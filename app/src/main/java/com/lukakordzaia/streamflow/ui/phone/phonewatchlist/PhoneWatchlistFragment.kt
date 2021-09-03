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
import com.lukakordzaia.streamflow.network.LoadingState
import com.lukakordzaia.streamflow.ui.baseclasses.BaseFragment
import com.lukakordzaia.streamflow.ui.shared.WatchlistViewModel
import com.lukakordzaia.streamflow.utils.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class PhoneWatchlistFragment : BaseFragment<FragmentPhoneWatchlistBinding>() {
    private val watchlistViewModel: WatchlistViewModel by viewModel()
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
    }

    private fun authCheck() {
        if (sharedPreferences.getLoginToken() != "") {
            watchlistViewModel.getUserWatchlist(1)
            binding.favoriteMoviesContainer.setVisible()
            binding.favoriteNoAuth.setGone()
        } else {
            binding.favoriteMoviesContainer.setGone()
            binding.favoriteNoAuth.setVisible()
        }
    }

    private fun fragmentListeners() {
        binding.toolbar.homeProfile.setOnClickListener {
            watchlistViewModel.onProfileButtonPressed()
        }

        binding.profileButton.setOnClickListener {
            watchlistViewModel.onProfileButtonPressed()
        }
    }

    private fun fragmentObservers() {
        watchlistViewModel.userWatchlist.observe(viewLifecycleOwner, { watchlist ->
            watchlistMoviesAdapter.setItems(watchlist)
        })

        watchlistViewModel.toastMessage.observe(viewLifecycleOwner, EventObserver {
            requireContext().createToast(it)
        })

        watchlistViewModel.navigateScreen.observe(viewLifecycleOwner, EventObserver {
            navController(it)
        })
    }

    private fun favMoviesContainer() {
        watchlistViewModel.watchListLoader.observe(viewLifecycleOwner, {
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

        watchlistViewModel.noFavorites.observe(viewLifecycleOwner, {
            if (it) {
                binding.favoriteNoMovies.setVisible()
            }
        })

        val moviesLayout = GridLayoutManager(requireActivity(), 2, GridLayoutManager.VERTICAL, false)
        watchlistMoviesAdapter = WatchlistAdapter(requireContext(),
            {
                watchlistViewModel.onSingleTitlePressed(it)
            },
            { titleId: Int ->
                removeTitleDialog(titleId)
            }
        )
        binding.rvFavoritesMovies.layoutManager = moviesLayout
        binding.rvFavoritesMovies.adapter = watchlistMoviesAdapter
    }

    private fun removeTitleDialog(titleId: Int) {
        val binding = DialogRemoveFavoriteBinding.inflate(LayoutInflater.from(requireContext()))
        val removeFavorite = Dialog(requireContext())
        removeFavorite.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        removeFavorite.setContentView(binding.root)

        binding.confirmButton.setOnClickListener {
            watchlistViewModel.deleteWatchlistTitle(titleId)
            removeFavorite.dismiss()
        }
        binding.cancelButton.setOnClickListener {
            removeFavorite.dismiss()
        }
        removeFavorite.show()
    }
}