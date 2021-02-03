package com.lukakordzaia.imoviesapp.ui.phone.genres

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.lukakordzaia.imoviesapp.R
import com.lukakordzaia.imoviesapp.utils.EventObserver
import com.lukakordzaia.imoviesapp.utils.navController
import com.lukakordzaia.imoviesapp.utils.setGone
import com.lukakordzaia.imoviesapp.utils.setVisible
import kotlinx.android.synthetic.main.phone_genres_framgent.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class GenresFragment : Fragment(R.layout.phone_genres_framgent) {
    private val genresViewModel by viewModel<GenresViewModel>()
    private lateinit var genresAdapter: GenresAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        genresViewModel.getAllGenres()

        genresViewModel.isLoading.observe(viewLifecycleOwner, EventObserver {
            if (!it) {
                genres_progressBar.setGone()
                rv_genres.setVisible()
            }
        })

        genresAdapter = GenresAdapter(requireContext()) {
            genresViewModel.onSingleGenrePressed(it)
        }
        rv_genres.adapter = genresAdapter

        genresViewModel.allGenresList.observe(viewLifecycleOwner, {
            genresAdapter.setGenreList(it)
        })

        genresViewModel.navigateScreen.observe(viewLifecycleOwner, EventObserver {
            navController(it)
        })
    }
}