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
import com.lukakordzaia.streamflow.network.LoadingState
import com.lukakordzaia.streamflow.ui.baseclasses.BaseFragment
import com.lukakordzaia.streamflow.utils.*
import kotlinx.android.synthetic.main.clear_db_alert_dialog.*
import kotlinx.android.synthetic.main.phone_favorites_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesFragment : BaseFragment() {
    private val favoritesViewModel: FavoritesViewModel by viewModel()
    private lateinit var favoritesMoviesAdapter: FavoritesAdapter
    private lateinit var favoriteTvShowsAdapter: FavoritesAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return getPersistentView(inflater, container, savedInstanceState, R.layout.phone_favorites_fragment)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!hasInitializedRootView) {
            Log.d("onviewcreated", "true")
            hasInitializedRootView = true
        }

        topBarListener("ფავორიტები")

        if (auth.currentUser != null) {
            favoritesViewModel.getFavTitlesFromFirestore()
            favorite_movies_container.setVisible()
            favorite_tvshows_container.setVisible()
            favorite_no_auth.setGone()
        } else {
            favorite_movies_container.setGone()
            favorite_tvshows_container.setGone()
            favorite_no_auth.setVisible()

            favorite_go_to_profile.setOnClickListener {
                favoritesViewModel.onProfileButtonPressed()
            }
        }

        // Favorite Movies
        favoritesViewModel.favoriteMoviesLoader.observe(viewLifecycleOwner, {
            when (it.status) {
                LoadingState.Status.RUNNING -> favorite_movies_progressBar.setVisible()
                LoadingState.Status.SUCCESS -> favorite_movies_progressBar.setGone()
            }
        })

        favoritesViewModel.favoriteNoMovies.observe(viewLifecycleOwner, {
            if (it) {
                favorite_no_movies.setVisible()
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
        rv_favorites_movies.layoutManager = moviesLayout
        rv_favorites_movies.adapter = favoritesMoviesAdapter


        favoritesViewModel.movieResult.observe(viewLifecycleOwner, {
            favoritesMoviesAdapter.setFavoritesTitleList(it)
        })



        // Favorite TV Shows
        favoritesViewModel.favoriteTvShowsLoader.observe(viewLifecycleOwner, {
            when (it.status) {
                LoadingState.Status.RUNNING -> favorite_tvshows_progressBar.setVisible()
                LoadingState.Status.SUCCESS -> favorite_tvshows_progressBar.setGone()
            }
        })

        favoritesViewModel.favoriteNoTvShows.observe(viewLifecycleOwner, {
            if (it) {
                favorite_no_tvshows.setVisible()
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
        rv_favorites_tvshows.layoutManager = tvShowsLayout
        rv_favorites_tvshows.adapter = favoriteTvShowsAdapter

        favoritesViewModel.tvShowResult.observe(viewLifecycleOwner, {
            favoriteTvShowsAdapter.setFavoritesTitleList(it)
        })


        favoritesViewModel.toastMessage.observe(viewLifecycleOwner, EventObserver {
            requireContext().createToast(it)
        })

        favoritesViewModel.navigateScreen.observe(viewLifecycleOwner, EventObserver {
            navController(it)
        })
    }
}