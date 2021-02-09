package com.lukakordzaia.imoviesapp.ui.phone.home

import android.app.Dialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.lukakordzaia.imoviesapp.R
import com.lukakordzaia.imoviesapp.network.LoadingState
import com.lukakordzaia.imoviesapp.utils.*
import kotlinx.android.synthetic.main.clear_db_alert_dialog.*
import kotlinx.android.synthetic.main.phone_home_framgent.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class HomeFragment : Fragment(R.layout.phone_home_framgent) {
    private val viewModel by viewModel<HomeViewModel>()

    private lateinit var homeDbTitlesAdapter: HomeDbTitlesAdapter
    private lateinit var homeNewMovieAdapter: HomeNewMovieAdapter
    private lateinit var homeTopMovieAdapter: HomeTopMovieAdapter
    private lateinit var homeTvShowAdapter: HomeTvShowAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        //Watched Titles List
        val dbLayout = GridLayoutManager(requireActivity(), 1, GridLayoutManager.HORIZONTAL, false)
        homeDbTitlesAdapter = HomeDbTitlesAdapter(requireContext(),
                {
                    viewModel.onDbTitlePressed(it)
                },
                {
                    viewModel.onSingleTitlePressed(AppConstants.NAV_HOME_TO_SINGLE, it)
                },
                { titleId: Int, buttonView: View ->
                    val popUp = PopupMenu(context, buttonView)
                    popUp.menuInflater.inflate(R.menu.watched_menu, popUp.menu)

                    popUp.setOnMenuItemClickListener {
                        when (it.itemId) {
                            R.id.delete_from_db -> {
                                val clearDbDialog = Dialog(requireContext())
                                clearDbDialog.setContentView(layoutInflater.inflate(R.layout.clear_db_alert_dialog, null))
                                clearDbDialog.clear_db_alert_yes.setOnClickListener {
                                    viewModel.deleteSingleTitleFromDb(requireContext(), titleId)
                                    clearDbDialog.dismiss()
                                }
                                clearDbDialog.clear_db_alert_no.setOnClickListener {
                                    clearDbDialog.dismiss()
                                }
                                clearDbDialog.show()
                                return@setOnMenuItemClickListener true
                            }
                            R.id.watched_check_info -> {
                                viewModel.onSingleTitlePressed(AppConstants.NAV_HOME_TO_SINGLE, titleId)
                                return@setOnMenuItemClickListener true
                            }
                            else -> {
                                requireContext().createToast("nothing else")
                                return@setOnMenuItemClickListener true
                            }
                        }
                    }
                    popUp.show()
                })
        rv_main_watched_titles.adapter = homeDbTitlesAdapter
        rv_main_watched_titles.layoutManager = dbLayout

        viewModel.getDbTitles(requireContext()).observe(viewLifecycleOwner, {
            if (!it.isNullOrEmpty()) {
                main_watched_titles_none.setGone()
                val newMoviesTitleConstraint = main_new_movies_title.layoutParams as ConstraintLayout.LayoutParams
                newMoviesTitleConstraint.topToBottom = rv_main_watched_titles.id
                main_new_movies_title.requestLayout()

                val newMoviesMoreConstraint = new_movies_more.layoutParams as ConstraintLayout.LayoutParams
                newMoviesMoreConstraint.topToBottom = rv_main_watched_titles.id
                new_movies_more.requestLayout()

                viewModel.getDbTitlesFromApi(it)
            } else {
                main_watched_titles_none.setVisible()
                viewModel.clearWatchedTitleList()

                val newMoviesTitleConstraint = main_new_movies_title.layoutParams as ConstraintLayout.LayoutParams
                newMoviesTitleConstraint.topToBottom = main_watched_titles_none.id
                main_new_movies_title.requestLayout()

                val newMoviesMoreConstraint = new_movies_more.layoutParams as ConstraintLayout.LayoutParams
                newMoviesMoreConstraint.topToBottom = main_watched_titles_none.id
                new_movies_more.requestLayout()
            }
        })

        viewModel.dbList.observe(viewLifecycleOwner, {
            homeDbTitlesAdapter.setWatchedTitlesList(it)
        })

        //New Movies List
        viewModel.newMovieLoader.observe(viewLifecycleOwner, {
            when (it.status) {
                LoadingState.Status.RUNNING -> newMovie_progressBar.setVisible()
                LoadingState.Status.SUCCESS -> newMovie_progressBar.setGone()
            }
        })


        val newMovieLayout = GridLayoutManager(requireActivity(), 1, GridLayoutManager.HORIZONTAL, false)
        homeNewMovieAdapter = HomeNewMovieAdapter(requireContext()) {
            viewModel.onSingleTitlePressed(AppConstants.NAV_HOME_TO_SINGLE, it)
        }
        rv_main_new_movies.adapter = homeNewMovieAdapter
        rv_main_new_movies.layoutManager = newMovieLayout

        viewModel.getNewMovies(1)
        viewModel.newMovieList.observe(viewLifecycleOwner, {
            homeNewMovieAdapter.setMoviesList(it)
        })

        //Top Movies List
        viewModel.topMovieLoader.observe(viewLifecycleOwner, {
            when (it.status) {
                LoadingState.Status.RUNNING -> topMovie_progressBar.setVisible()
                LoadingState.Status.SUCCESS -> topMovie_progressBar.setGone()
            }
        })

        val topMovieLayout = GridLayoutManager(requireActivity(), 1, GridLayoutManager.HORIZONTAL, false)
        homeTopMovieAdapter = HomeTopMovieAdapter(requireContext()) {
            viewModel.onSingleTitlePressed(AppConstants.NAV_HOME_TO_SINGLE, it)
        }
        rv_main_top_movies.adapter = homeTopMovieAdapter
        rv_main_top_movies.layoutManager = topMovieLayout

        viewModel.getTopMovies(1)

        viewModel.topMovieList.observe(viewLifecycleOwner, Observer {
            homeTopMovieAdapter.setMoviesList(it)
        })


        //Top TvShows List
        viewModel.topTvShowsLoader.observe(viewLifecycleOwner, {
            when (it.status) {
                LoadingState.Status.RUNNING -> topTvShow_progressBar.setVisible()
                LoadingState.Status.SUCCESS -> topTvShow_progressBar.setGone()
            }
        })

        val tvShowLayout = GridLayoutManager(requireActivity(), 1, GridLayoutManager.HORIZONTAL, false)
        homeTvShowAdapter = HomeTvShowAdapter(requireContext()) {
            viewModel.onSingleTitlePressed(AppConstants.NAV_HOME_TO_SINGLE, it)
        }
        rv_main_top_tvshows.adapter = homeTvShowAdapter
        rv_main_top_tvshows.layoutManager = tvShowLayout

        viewModel.getTopTvShows(1)

        viewModel.topTvShowList.observe(viewLifecycleOwner, Observer {
            homeTvShowAdapter.setTvShowsList(it)
        })

        top_movies_more.setOnClickListener {
            viewModel.topMoviesMorePressed()
        }

        top_tvshows_more.setOnClickListener {
            viewModel.topTvShowsMorePressed()
        }

        viewModel.navigateScreen.observe(viewLifecycleOwner, EventObserver {
            navController(it)
        })

        viewModel.toastMessage.observe(viewLifecycleOwner, EventObserver {
            requireContext().createToast(it)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_fragment_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settings_button -> {
                navController(HomeFragmentDirections.actionHomeFragmentToSettingsFragment())
            }
        }
        return super.onOptionsItemSelected(item)
    }
}