package com.lukakordzaia.streamflow.ui.phone.categories

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.utils.EventObserver
import com.lukakordzaia.streamflow.utils.navController
import com.lukakordzaia.streamflow.utils.setGone
import com.lukakordzaia.streamflow.utils.setVisible
import kotlinx.android.synthetic.main.phone_categories_framgent.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class CategoriesFragment : Fragment(R.layout.phone_categories_framgent) {
    private val categoriesViewModel by viewModel<CategoriesViewModel>()
    private lateinit var genresAdapter: GenresAdapter
    private lateinit var studiosAdapter: StudiosAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        categoriesViewModel.isLoading.observe(viewLifecycleOwner, EventObserver {
            if (!it) {
                categories_progressBar.setGone()
                rv_genres.setVisible()
                rv_studios.setVisible()
            }
        })

        // Genres
        categoriesViewModel.getAllGenres()

        val genreLayout = GridLayoutManager(requireActivity(), 1, GridLayoutManager.HORIZONTAL, false)
        genresAdapter = GenresAdapter(requireContext()) {
            categoriesViewModel.onSingleGenrePressed(it)
        }
        rv_genres.layoutManager = genreLayout
        rv_genres.adapter = genresAdapter

        categoriesViewModel.allGenresList.observe(viewLifecycleOwner, {
            genresAdapter.setGenreList(it)
        })


        // Studios
        categoriesViewModel.getTopStudios()

        val studioLayout = GridLayoutManager(requireActivity(), 1, GridLayoutManager.HORIZONTAL, false)
        studiosAdapter = StudiosAdapter(requireContext()) {
            categoriesViewModel.onSingleStudioPressed(it)
        }
        rv_studios.layoutManager = studioLayout
        rv_studios.adapter = studiosAdapter

        categoriesViewModel.topStudioList.observe(viewLifecycleOwner, {
            studiosAdapter.setStudioList(it)
        })


        categoriesViewModel.navigateScreen.observe(viewLifecycleOwner, EventObserver {
            navController(it)
        })
    }
}