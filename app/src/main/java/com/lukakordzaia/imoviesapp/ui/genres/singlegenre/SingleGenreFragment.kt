package com.lukakordzaia.imoviesapp.ui.genres.singlegenre

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.lukakordzaia.imoviesapp.R
import com.lukakordzaia.imoviesapp.utils.EventObserver
import com.lukakordzaia.imoviesapp.utils.navController
import kotlinx.android.synthetic.main.fragment_single_genre.*

class SingleGenreFragment : Fragment(R.layout.fragment_single_genre) {
    private lateinit var viewModel: SingleGenreViewModel
    private lateinit var singleGenreAdapter: SingleGenreAdapter
    private val args: SingleGenreFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(SingleGenreViewModel::class.java)
        viewModel.getSingleGenre(args.genreId)

        singleGenreAdapter = SingleGenreAdapter(requireContext()) {
            viewModel.onSingleTitlePressed(it)
        }
        rv_single_genre.adapter = singleGenreAdapter

        viewModel.singleGenreList.observe(viewLifecycleOwner, {
            singleGenreAdapter.setGenreTitleList(it)
        })

        viewModel.navigateScreen.observe(viewLifecycleOwner, EventObserver {
            navController(it)
        })
    }
}