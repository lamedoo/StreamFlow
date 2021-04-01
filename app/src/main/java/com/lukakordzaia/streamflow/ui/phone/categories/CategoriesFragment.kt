package com.lukakordzaia.streamflow.ui.phone.categories

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.datamodels.VideoPlayerData
import com.lukakordzaia.streamflow.helpers.DotsIndicatorDecoration
import com.lukakordzaia.streamflow.network.LoadingState
import com.lukakordzaia.streamflow.ui.phone.videoplayer.VideoPlayerActivity
import com.lukakordzaia.streamflow.utils.*
import kotlinx.android.synthetic.main.main_top_toolbar.*
import kotlinx.android.synthetic.main.phone_categories_framgent.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class CategoriesFragment : Fragment(R.layout.phone_categories_framgent) {
    private val categoriesViewModel by viewModel<CategoriesViewModel>()
    private lateinit var genresAdapter: GenresAdapter
    private lateinit var studiosAdapter: StudiosAdapter
    private lateinit var trailersAdapter: TrailersAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        home_favorites.setGone()
        home_profile.setGone()

        categoriesViewModel.noInternet.observe(viewLifecycleOwner, EventObserver {
            if (it) {
                requireContext().createToast(AppConstants.NO_INTERNET)
                Handler(Looper.getMainLooper()).postDelayed({
                    categoriesViewModel.refreshContent()
                }, 5000)
            }
        })

        // Trailers
        categoriesViewModel.getTopTrailers()

        categoriesViewModel.trailersLoader.observe(viewLifecycleOwner, {
            when (it.status) {
                LoadingState.Status.RUNNING -> trailers_progressBar.setVisible()
                LoadingState.Status.SUCCESS -> trailers_progressBar.setGone()
            }
        })

        val trailerLayout = GridLayoutManager(requireActivity(), 1, GridLayoutManager.HORIZONTAL, false)
        trailersAdapter = TrailersAdapter(requireContext(),
                { titleId, trailerUrl ->
//                    categoriesViewModel.onSingleTrailerPressed(titleId, trailerUrl)
                    val intent = Intent(context, VideoPlayerActivity::class.java)
                    intent.putExtra("videoPlayerData", VideoPlayerData(
                        titleId,
                        false,
                        0,
                        "ENG",
                        0,
                        0L,
                        trailerUrl
                    ))
                    activity?.startActivity(intent)
                },
                {
                    categoriesViewModel.onSingleTrailerInfoPressed(it)
                }
        )
        rv_trailers.layoutManager = trailerLayout
        rv_trailers.adapter = trailersAdapter

        categoriesViewModel.topTrailerList.observe(viewLifecycleOwner, {
            trailersAdapter.setTrailerList(it)
        })

        val helper: SnapHelper = PagerSnapHelper()
        helper.attachToRecyclerView(rv_trailers)

        val radius = resources.getDimensionPixelSize(R.dimen.radius);
        val dotsHeight = resources.getDimensionPixelSize(R.dimen.dots_height);
        val color = ContextCompat.getColor(requireContext(), R.color.general_text_color);

        rv_trailers.addItemDecoration(DotsIndicatorDecoration(radius, radius * 4, dotsHeight, color, color))


        // Genres
        categoriesViewModel.getAllGenres()

        categoriesViewModel.genresLoader.observe(viewLifecycleOwner, {
            when (it.status) {
                LoadingState.Status.RUNNING -> genres_progressBar.setVisible()
                LoadingState.Status.SUCCESS -> genres_progressBar.setGone()
            }
        })

        val genreLayout = GridLayoutManager(requireActivity(), 1, GridLayoutManager.HORIZONTAL, false)
        genresAdapter = GenresAdapter(requireContext()) { genreId: Int, genreName: String ->
            categoriesViewModel.onSingleGenrePressed(genreId, genreName)
        }
        rv_genres.layoutManager = genreLayout
        rv_genres.adapter = genresAdapter

        categoriesViewModel.allGenresList.observe(viewLifecycleOwner, {
            genresAdapter.setGenreList(it)
        })


        // Studios
        categoriesViewModel.getTopStudios()

        categoriesViewModel.studiosLoader.observe(viewLifecycleOwner, {
            when (it.status) {
                LoadingState.Status.RUNNING -> studios_progressBar.setVisible()
                LoadingState.Status.SUCCESS -> studios_progressBar.setGone()
            }
        })

        val studioLayout = GridLayoutManager(requireActivity(), 1, GridLayoutManager.HORIZONTAL, false)
        studiosAdapter = StudiosAdapter(requireContext()) { studioId: Int, studioName: String ->
            categoriesViewModel.onSingleStudioPressed(studioId, studioName)
        }
        rv_studios.layoutManager = studioLayout
        rv_studios.adapter = studiosAdapter

        categoriesViewModel.topStudioList.observe(viewLifecycleOwner, {
            studiosAdapter.setStudioList(it)
        })


        categoriesViewModel.navigateScreen.observe(viewLifecycleOwner, EventObserver {
            navController(it)
        })

        categoriesViewModel.toastMessage.observe(viewLifecycleOwner, EventObserver {
            requireContext().createToast(it)
        })
    }
}