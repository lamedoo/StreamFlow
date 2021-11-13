package com.lukakordzaia.streamflow.ui.phone.phonewatchlist

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lukakordzaia.core.AppConstants
import com.lukakordzaia.core.network.LoadingState
import com.lukakordzaia.core.utils.setColor
import com.lukakordzaia.core.utils.setGone
import com.lukakordzaia.core.utils.setVisible
import com.lukakordzaia.core.utils.setVisibleOrGone
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.databinding.DialogRemoveFavoriteBinding
import com.lukakordzaia.streamflow.databinding.FragmentPhoneWatchlistBinding
import com.lukakordzaia.streamflow.ui.baseclasses.fragments.BaseFragmentPhoneVM
import org.koin.androidx.viewmodel.ext.android.viewModel

class PhoneWatchlistFragment : BaseFragmentPhoneVM<FragmentPhoneWatchlistBinding, WatchlistViewModel>() {
    private var page = 1

    override val viewModel by viewModel<WatchlistViewModel>()
    override val reload: () -> Unit = { viewModel.getUserWatchlist(page, AppConstants.WATCHLIST_MOVIES, false) }

    private lateinit var watchlistMoviesAdapter: WatchlistAdapter
    private var type = AppConstants.WATCHLIST_MOVIES

    private var pastVisibleItems: Int = 0
    private var visibleItemCount: Int = 0
    private var totalItemCount: Int = 0
    private var loading = false
    private var hasMore = false

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentPhoneWatchlistBinding
        get() = FragmentPhoneWatchlistBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authCheck()
        fragmentListeners()
        fragmentObservers()
        favMoviesContainer()
        setButtons(AppConstants.WATCHLIST_MOVIES)
    }

    private fun authCheck() {
        if (sharedPreferences.getLoginToken() != "") {
            viewModel.getUserWatchlist(page, AppConstants.WATCHLIST_MOVIES, false)
            binding.favoriteMoviesContainer.setVisible()
            binding.favoriteNoAuth.setGone()
        } else {
            binding.favoriteMoviesContainer.setGone()
            binding.favoriteNoAuth.setVisible()
        }
    }

    private fun fragmentListeners() {
        binding.toolbar.homeProfile.setOnClickListener {
            viewModel.onProfileButtonPressed()
        }

        binding.profileButton.setOnClickListener {
            viewModel.onProfileButtonPressed()
        }

        binding.watchlistMovies.setOnClickListener {
            changeWatchlistType(AppConstants.WATCHLIST_MOVIES)
        }

        binding.watchlistTvShows.setOnClickListener {
            changeWatchlistType(AppConstants.WATCHLIST_TV_SHOWS)
        }
    }

    private fun fragmentObservers() {
        viewModel.generalLoader.observe(viewLifecycleOwner, {
            binding.favoriteMoviesProgressBar.setVisibleOrGone(it == LoadingState.LOADING)
            loading = it != LoadingState.LOADED
        })

        viewModel.userWatchlist.observe(viewLifecycleOwner, { watchlist ->
            watchlistMoviesAdapter.setItems(watchlist)
        })

        viewModel.hasMorePage.observe(viewLifecycleOwner, {
            hasMore = it
        })

        viewModel.noFavorites.observe(viewLifecycleOwner, {
            binding.favoriteNoMovies.setVisibleOrGone(it)
        })
    }

    private fun favMoviesContainer() {
        val moviesLayout = GridLayoutManager(requireActivity(), 2, GridLayoutManager.VERTICAL, false)
        watchlistMoviesAdapter = WatchlistAdapter(requireContext(),
            {
                viewModel.onSingleTitlePressed(it)
            },
            { titleId: Int, position: Int ->
                removeTitleDialog(titleId, position)
            }
        )
        binding.rvFavoritesMovies.layoutManager = moviesLayout
        binding.rvFavoritesMovies.adapter = watchlistMoviesAdapter

        binding.rvFavoritesMovies.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    visibleItemCount = moviesLayout.childCount
                    totalItemCount = moviesLayout.itemCount
                    pastVisibleItems = moviesLayout.findFirstVisibleItemPosition()

                    if (!loading && (visibleItemCount + pastVisibleItems) >= totalItemCount) {
                        loading = true
                        fetchMoreTitle()
                    }
                }
            }
        })
    }

    private fun changeWatchlistType(type: String) {
        viewModel.clearWatchlist()

        page = 1
        viewModel.getUserWatchlist(page, type, false)
        setButtons(type)

        this.type = type
    }

    private fun removeTitleDialog(titleId: Int, position: Int) {
        val binding = DialogRemoveFavoriteBinding.inflate(LayoutInflater.from(requireContext()))
        val removeFavorite = Dialog(requireContext())
        removeFavorite.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        removeFavorite.setContentView(binding.root)

        binding.confirmButton.setOnClickListener {
            viewModel.deleteWatchlistTitle(titleId, position)
            removeFavorite.dismiss()
        }
        binding.cancelButton.setOnClickListener {
            removeFavorite.dismiss()
        }
        removeFavorite.show()
    }

    private fun fetchMoreTitle() {
        if (hasMore) {
            binding.favoriteMoviesProgressBar.setVisible()
            page++
            viewModel.getUserWatchlist(page, type, false)
        }
    }

    private fun setButtons(type: String) {
        when (type) {
            AppConstants.WATCHLIST_MOVIES -> {
                binding.watchlistMovies.setColor(R.color.accent_color)
                binding.watchlistTvShows.setColor(R.color.secondary_color)
            }
            AppConstants.WATCHLIST_TV_SHOWS -> {
                binding.watchlistMovies.setColor(R.color.secondary_color)
                binding.watchlistTvShows.setColor(R.color.accent_color)
            }
        }
    }
}