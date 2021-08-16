package com.lukakordzaia.streamflow.ui.phone.favorites

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.databinding.DialogRemoveFavoriteBinding
import com.lukakordzaia.streamflow.databinding.FragmentPhoneFavoritesBinding
import com.lukakordzaia.streamflow.network.LoadingState
import com.lukakordzaia.streamflow.ui.baseclasses.BaseFragment
import com.lukakordzaia.streamflow.utils.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class PhoneFavoritesFragment : BaseFragment<FragmentPhoneFavoritesBinding>() {
    private val phoneFavoritesViewModel: PhoneFavoritesViewModel by viewModel()
    private lateinit var favoritesMoviesAdapter: FavoritesAdapter
    private lateinit var favoriteTvShowsAdapter: FavoritesAdapter

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentPhoneFavoritesBinding
        get() = FragmentPhoneFavoritesBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        topBarListener(resources.getString(R.string.favorites), binding.toolbar)

        authCheck()
        fragmentListeners()
        fragmentObservers()
        favMoviesContainer()
        favTvShowsContainer()
    }

    private fun authCheck() {
        if (auth.currentUser != null) {
            phoneFavoritesViewModel.getFavTitlesFromFirestore()
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
        binding.profileButton.setOnClickListener {
            phoneFavoritesViewModel.onProfileButtonPressed()
        }
    }

    private fun fragmentObservers() {
        phoneFavoritesViewModel.toastMessage.observe(viewLifecycleOwner, EventObserver {
            requireContext().createToast(it)
        })

        phoneFavoritesViewModel.navigateScreen.observe(viewLifecycleOwner, EventObserver {
            navController(it)
        })
    }

    private fun favMoviesContainer() {
        phoneFavoritesViewModel.favoriteMoviesLoader.observe(viewLifecycleOwner, {
            when (it.status) {
                LoadingState.Status.RUNNING -> binding.favoriteMoviesProgressBar.setVisible()
                LoadingState.Status.SUCCESS -> binding.favoriteMoviesProgressBar.setGone()
            }
        })

        phoneFavoritesViewModel.favoriteNoMovies.observe(viewLifecycleOwner, {
            if (it) {
                binding.favoriteNoMovies.setVisible()
            }
        })

        val moviesLayout = GridLayoutManager(requireActivity(), 1, GridLayoutManager.HORIZONTAL, false)
        favoritesMoviesAdapter = FavoritesAdapter(requireContext(),
            {
                phoneFavoritesViewModel.onSingleTitlePressed(it)
            },
            { titleId: Int ->
                removeTitleDialog(titleId)
            }
        )
        binding.rvFavoritesMovies.layoutManager = moviesLayout
        binding.rvFavoritesMovies.adapter = favoritesMoviesAdapter


        phoneFavoritesViewModel.movieResult.observe(viewLifecycleOwner, {
            favoritesMoviesAdapter.setItems(it)
        })
    }

    private fun favTvShowsContainer() {
        phoneFavoritesViewModel.favoriteTvShowsLoader.observe(viewLifecycleOwner, {
            when (it.status) {
                LoadingState.Status.RUNNING -> binding.favoriteTvshowsProgressBar.setVisible()
                LoadingState.Status.SUCCESS -> binding.favoriteTvshowsProgressBar.setGone()
            }
        })

        phoneFavoritesViewModel.favoriteNoTvShows.observe(viewLifecycleOwner, {
            if (it) {
                binding.favoriteNoTvshows.setVisible()
            }
        })

        val tvShowsLayout = GridLayoutManager(requireActivity(), 1, GridLayoutManager.HORIZONTAL, false)
        favoriteTvShowsAdapter = FavoritesAdapter(requireContext(),
            {
                phoneFavoritesViewModel.onSingleTitlePressed(it)
            },
            { titleId: Int ->
                removeTitleDialog(titleId)
            }
        )
        binding.rvFavoritesTvshows.layoutManager = tvShowsLayout
        binding.rvFavoritesTvshows.adapter = favoriteTvShowsAdapter

        phoneFavoritesViewModel.tvShowResult.observe(viewLifecycleOwner, {
            favoriteTvShowsAdapter.setItems(it)
        })
    }

    private fun removeTitleDialog(titleId: Int) {
        val binding = DialogRemoveFavoriteBinding.inflate(LayoutInflater.from(requireContext()))
        val removeFavorite = Dialog(requireContext())
        removeFavorite.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        removeFavorite.setContentView(binding.root)

        binding.confirmButton.setOnClickListener {
            phoneFavoritesViewModel.removeFavTitleFromFirestore(titleId)
            removeFavorite.dismiss()
        }
        binding.cancelButton.setOnClickListener {
            removeFavorite.dismiss()
        }
        removeFavorite.show()
    }
}