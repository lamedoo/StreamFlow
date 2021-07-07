package com.lukakordzaia.streamflow.ui.phone.favorites

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.databinding.FragmentPhoneFavoritesBinding
import com.lukakordzaia.streamflow.network.LoadingState
import com.lukakordzaia.streamflow.ui.baseclasses.BaseFragment
import com.lukakordzaia.streamflow.utils.*
import kotlinx.android.synthetic.main.clear_db_alert_dialog.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesFragment : BaseFragment<FragmentPhoneFavoritesBinding>() {
    private val favoritesViewModel: FavoritesViewModel by viewModel()
    private lateinit var favoritesMoviesAdapter: FavoritesAdapter
    private lateinit var favoriteTvShowsAdapter: FavoritesAdapter

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentPhoneFavoritesBinding
        get() = FragmentPhoneFavoritesBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!hasInitializedRootView) {
            hasInitializedRootView = true
        }

        topBarListener("ფავორიტები")

        authCheck()
        fragmentListeners()
        fragmentObservers()
        favMoviesContainer()
        favTvShowsContainer()
    }

    private fun authCheck() {
        if (auth.currentUser != null) {
            favoritesViewModel.getFavTitlesFromFirestore()
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
            favoritesViewModel.onProfileButtonPressed()
        }
    }

    private fun fragmentObservers() {
        favoritesViewModel.toastMessage.observe(viewLifecycleOwner, EventObserver {
            requireContext().createToast(it)
        })

        favoritesViewModel.navigateScreen.observe(viewLifecycleOwner, EventObserver {
            navController(it)
        })
    }

    private fun favMoviesContainer() {
        favoritesViewModel.favoriteMoviesLoader.observe(viewLifecycleOwner, {
            when (it.status) {
                LoadingState.Status.RUNNING -> binding.favoriteMoviesProgressBar.setVisible()
                LoadingState.Status.SUCCESS -> binding.favoriteMoviesProgressBar.setGone()
            }
        })

        favoritesViewModel.favoriteNoMovies.observe(viewLifecycleOwner, {
            if (it) {
                binding.favoriteNoMovies.setVisible()
            }
        })

        val moviesLayout = GridLayoutManager(requireActivity(), 1, GridLayoutManager.HORIZONTAL, false)
        favoritesMoviesAdapter = FavoritesAdapter(requireContext(),
            {
                favoritesViewModel.onSingleTitlePressed(it)
            },
            { titleId: Int ->
                val clearDbDialog = Dialog(requireContext())
                clearDbDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                clearDbDialog.setContentView(layoutInflater.inflate(R.layout.remove_favorite_alert_dialog, null))
                clearDbDialog.clear_db_alert_yes.setOnClickListener {
                    favoritesViewModel.removeFavTitleFromFirestore(titleId)
                    clearDbDialog.dismiss()
                }
                clearDbDialog.clear_db_alert_no.setOnClickListener {
                    clearDbDialog.dismiss()
                }
                clearDbDialog.show()
            })
        binding.rvFavoritesMovies.layoutManager = moviesLayout
        binding.rvFavoritesMovies.adapter = favoritesMoviesAdapter


        favoritesViewModel.movieResult.observe(viewLifecycleOwner, {
            favoritesMoviesAdapter.setFavoritesTitleList(it)
        })
    }

    private fun favTvShowsContainer() {
        favoritesViewModel.favoriteTvShowsLoader.observe(viewLifecycleOwner, {
            when (it.status) {
                LoadingState.Status.RUNNING -> binding.favoriteTvshowsProgressBar.setVisible()
                LoadingState.Status.SUCCESS -> binding.favoriteTvshowsProgressBar.setGone()
            }
        })

        favoritesViewModel.favoriteNoTvShows.observe(viewLifecycleOwner, {
            if (it) {
                binding.favoriteNoTvshows.setVisible()
            }
        })

        val tvShowsLayout = GridLayoutManager(requireActivity(), 1, GridLayoutManager.HORIZONTAL, false)
        favoriteTvShowsAdapter = FavoritesAdapter(requireContext(),
            {
                favoritesViewModel.onSingleTitlePressed(it)
            },
            { titleId: Int ->
                val clearDbDialog = Dialog(requireContext())
                clearDbDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                clearDbDialog.setContentView(layoutInflater.inflate(R.layout.remove_favorite_alert_dialog, null))
                clearDbDialog.clear_db_alert_yes.setOnClickListener {
                    favoritesViewModel.removeFavTitleFromFirestore(titleId)
                    clearDbDialog.dismiss()
                }
                clearDbDialog.clear_db_alert_no.setOnClickListener {
                    clearDbDialog.dismiss()
                }
                clearDbDialog.show()
            })
        binding.rvFavoritesTvshows.layoutManager = tvShowsLayout
        binding.rvFavoritesTvshows.adapter = favoriteTvShowsAdapter

        favoritesViewModel.tvShowResult.observe(viewLifecycleOwner, {
            favoriteTvShowsAdapter.setFavoritesTitleList(it)
        })
    }
}