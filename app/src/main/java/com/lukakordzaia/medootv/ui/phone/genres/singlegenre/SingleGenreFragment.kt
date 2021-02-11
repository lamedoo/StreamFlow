package com.lukakordzaia.medootv.ui.phone.genres.singlegenre

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.lukakordzaia.medootv.R
import com.lukakordzaia.medootv.utils.*
import kotlinx.android.synthetic.main.phone_single_genre_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class SingleGenreFragment : Fragment(R.layout.phone_single_genre_fragment) {
    private val singleGenreViewModel by viewModel<SingleGenreViewModel>()
    private lateinit var singleGenreAdapter: SingleGenreAdapter
    private val args: SingleGenreFragmentArgs by navArgs()
    private var page = 1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        singleGenreViewModel.getSingleGenre(args.genreId, page)

        val layoutManager = GridLayoutManager(requireActivity(), 2, GridLayoutManager.VERTICAL, false)

        singleGenreViewModel.isLoading.observe(viewLifecycleOwner, EventObserver {
            if (!it) {
                single_genre_progressBar.setGone()
            }
        })

        singleGenreAdapter = SingleGenreAdapter(requireContext()) {
            singleGenreViewModel.onSingleTitlePressed(it)
        }
        rv_single_genre.adapter = singleGenreAdapter
        rv_single_genre.layoutManager = layoutManager

        singleGenreViewModel.singleGenreList.observe(viewLifecycleOwner, {
            singleGenreAdapter.setGenreTitleList(it)
        })

        singleGenreViewModel.hasMorePage.observe(viewLifecycleOwner, {
            if (it) {
                infiniteScroll(singlegenre_nested_scroll) { fetchMoreTitle() }
            }
        })

        singleGenreViewModel.navigateScreen.observe(viewLifecycleOwner, EventObserver {
            navController(it)
        })
    }

    private fun fetchMoreTitle() {
        single_genre_progressBar.setVisible()
        page++
        Log.d("currentpage", page.toString())
        singleGenreViewModel.getSingleGenre(args.genreId, page)
    }
}