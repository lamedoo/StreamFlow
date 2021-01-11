package com.lukakordzaia.imoviesapp.ui.phone.genres

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.lukakordzaia.imoviesapp.R
import com.lukakordzaia.imoviesapp.utils.EventObserver
import com.lukakordzaia.imoviesapp.utils.navController
import com.lukakordzaia.imoviesapp.utils.setGone
import com.lukakordzaia.imoviesapp.utils.setVisible
import kotlinx.android.synthetic.main.fragment_genres.*

class GenresFragment : Fragment(R.layout.fragment_genres) {
    private lateinit var viewModel: GenresViewModel
    private lateinit var genresAdapter: GenresAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(GenresViewModel::class.java)
        viewModel.getAllGenres()

        viewModel.isLoading.observe(viewLifecycleOwner, EventObserver {
            if (!it) {
                genres_progressBar.setGone()
                rv_genres.setVisible()
            }
        })

        genresAdapter = GenresAdapter(requireContext()) {
            viewModel.onSingleGenrePressed(it)
        }
        rv_genres.adapter = genresAdapter

        viewModel.allGenresList.observe(viewLifecycleOwner, {
            genresAdapter.setGenreList(it)
        })

        viewModel.navigateScreen.observe(viewLifecycleOwner, EventObserver {
            navController(it)
        })
    }
}