package com.lukakordzaia.imoviesapp.ui.phone.genres.singlegenre

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.lukakordzaia.imoviesapp.R
import com.lukakordzaia.imoviesapp.utils.*
import kotlinx.android.synthetic.main.phone_single_genre_fragment.*

class SingleGenreFragment : Fragment(R.layout.phone_single_genre_fragment) {
    private lateinit var viewModel: SingleGenreViewModel
    private lateinit var singleGenreAdapter: SingleGenreAdapter
    private val args: SingleGenreFragmentArgs by navArgs()
    private var page = 1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(SingleGenreViewModel::class.java)
        viewModel.getSingleGenre(args.genreId, page)

        val layoutManager = GridLayoutManager(requireActivity(), 2, GridLayoutManager.VERTICAL, false)

        viewModel.isLoading.observe(viewLifecycleOwner, EventObserver {
            if (!it) {
                single_genre_progressBar.setGone()
            }
        })

        singleGenreAdapter = SingleGenreAdapter(requireContext()) {
            viewModel.onSingleTitlePressed(it)
        }
        rv_single_genre.adapter = singleGenreAdapter
        rv_single_genre.layoutManager = layoutManager

        viewModel.singleGenreList.observe(viewLifecycleOwner, {
            singleGenreAdapter.setGenreTitleList(it)
        })

        viewModel.hasMorePage.observe(viewLifecycleOwner, {
            if (it) {
                infiniteScroll(singlegenre_nested_scroll) { fetchMoreTitle() }
            }
        })

        viewModel.navigateScreen.observe(viewLifecycleOwner, EventObserver {
            navController(it)
        })
    }

    private fun fetchMoreTitle() {
        single_genre_progressBar.setVisible()
        page++
        Log.d("currentpage", page.toString())
        viewModel.getSingleGenre(args.genreId, page)
    }
}