package com.lukakordzaia.imoviesapp.ui.phone.home

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.lukakordzaia.imoviesapp.R
import com.lukakordzaia.imoviesapp.utils.*
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : Fragment(R.layout.fragment_home) {
    private lateinit var viewModel: HomeViewModel
    private lateinit var homeWatchedAdapter: HomeWatchedAdapter
    private lateinit var homeMovieAdapter: HomeMovieAdapter
    private lateinit var homeTvShowAdapter: HomeTvShowAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        viewModel.isLoading.observe(viewLifecycleOwner, EventObserver {
            if (!it) {
                home_progressBar.setGone()
                home_main_container.setVisible()
            }
        })

        //Watched Titles List
        val watchedLayout = GridLayoutManager(requireActivity(), 1, GridLayoutManager.HORIZONTAL, false)
        homeWatchedAdapter = HomeWatchedAdapter(requireContext(),
                {
                    viewModel.onWatchedTitlePressed(it)
                },
                { titleId: Int, buttonView: View ->
                    val popUp = PopupMenu(context, buttonView)
                    popUp.menuInflater.inflate(R.menu.watched_menu, popUp.menu)

                    popUp.setOnMenuItemClickListener {
                        when (it.itemId) {
                            R.id.delete_from_db -> {
                                viewModel.deleteSingleTitleFromDb(requireContext(), titleId)
                                return@setOnMenuItemClickListener true
                            }
                            R.id.watched_check_info -> {
                                viewModel.onSingleTitlePressed("home", titleId)
                                return@setOnMenuItemClickListener true
                            }
                            else -> {
                                requireContext().createToast("aaaa")
                                return@setOnMenuItemClickListener true
                            }
                        }
                    }
                    popUp.show()
                })
        rv_main_watched_titles.adapter = homeWatchedAdapter
        rv_main_watched_titles.layoutManager = watchedLayout

        viewModel.getWatchedFromDb(requireContext()).observe(viewLifecycleOwner, {
            if (!it.isNullOrEmpty()) {
                main_watched_titles_none.setGone()
                val topMoviesTitleConstraint = main_top_movies_title.layoutParams as ConstraintLayout.LayoutParams
                topMoviesTitleConstraint.topToBottom = rv_main_watched_titles.id
                main_top_movies_title.requestLayout()

                val topMoviesMoreConstraint = top_movies_more.layoutParams as ConstraintLayout.LayoutParams
                topMoviesMoreConstraint.topToBottom = rv_main_watched_titles.id
                top_movies_more.requestLayout()

                viewModel.getWatchedTitles(it)
            } else {
                main_watched_titles_none.setVisible()
                viewModel.clearWatchedTitleList()

                val topMoviesTitleConstraint = main_top_movies_title.layoutParams as ConstraintLayout.LayoutParams
                topMoviesTitleConstraint.topToBottom = main_watched_titles_none.id
                main_top_movies_title.requestLayout()

                val topMoviesMoreConstraint = top_movies_more.layoutParams as ConstraintLayout.LayoutParams
                topMoviesMoreConstraint.topToBottom = main_watched_titles_none.id
                top_movies_more.requestLayout()
            }
        })

        viewModel.watchedList.observe(viewLifecycleOwner, {
            homeWatchedAdapter.setWatchedTitlesList(it)
        })

        //New Movies List
        val movieLayout = GridLayoutManager(requireActivity(), 1, GridLayoutManager.HORIZONTAL, false)
        homeMovieAdapter = HomeMovieAdapter(requireContext()) {
            viewModel.onSingleTitlePressed("home", it)
        }
        rv_main_top_movies.adapter =  homeMovieAdapter
        rv_main_top_movies.layoutManager = movieLayout

        viewModel.getTopMovies(1)

        viewModel.movieList.observe(viewLifecycleOwner, Observer {
            homeMovieAdapter.setMoviesList(it)
        })


        //New TvShows List
        val tvShowLayout = GridLayoutManager(requireActivity(), 1, GridLayoutManager.HORIZONTAL, false)
        homeTvShowAdapter = HomeTvShowAdapter(requireContext()) {
            viewModel.onSingleTitlePressed("home", it)
        }
        rv_main_top_tvshows.adapter = homeTvShowAdapter
        rv_main_top_tvshows.layoutManager = tvShowLayout

        viewModel.getTopTvShows(1)

        viewModel.tvShowList.observe(viewLifecycleOwner, Observer {
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