package com.lukakordzaia.imoviesapp.ui.phone.home.toplistfragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.lukakordzaia.imoviesapp.R
import com.lukakordzaia.imoviesapp.ui.phone.genres.singlegenre.SingleGenreAdapter
import com.lukakordzaia.imoviesapp.ui.phone.home.HomeViewModel
import com.lukakordzaia.imoviesapp.utils.*
import kotlinx.android.synthetic.main.phone_single_genre_fragment.*

class TopTvShowsFragment : Fragment(R.layout.phone_single_genre_fragment) {
    private lateinit var viewModel: HomeViewModel
    private lateinit var singleGenreAdapter: SingleGenreAdapter
    private var page = 1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        viewModel.getTopTvShows(page)

        val layoutManager = GridLayoutManager(requireActivity(), 2, GridLayoutManager.VERTICAL, false)

        viewModel.isLoading.observe(viewLifecycleOwner, EventObserver {
            if (!it) {
                single_genre_progressBar.setGone()
            }
        })

        singleGenreAdapter = SingleGenreAdapter(requireContext()) {
            viewModel.onSingleTitlePressed("topTvShows", it)
        }
        rv_single_genre.adapter = singleGenreAdapter
        rv_single_genre.layoutManager = layoutManager

        viewModel.tvShowList.observe(viewLifecycleOwner, {
            singleGenreAdapter.setGenreTitleList(it)
        })

        infiniteScroll(singlegenre_nested_scroll) { fetchMoreTopTvShows() }

        viewModel.navigateScreen.observe(viewLifecycleOwner, EventObserver {
            navController(it)
        })
    }

    private fun fetchMoreTopTvShows() {
        single_genre_progressBar.setVisible()
        page++
        Log.d("currentpage", page.toString())
        viewModel.getTopTvShows(page)
    }
}