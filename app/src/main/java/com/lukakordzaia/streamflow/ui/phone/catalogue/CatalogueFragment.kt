package com.lukakordzaia.streamflow.ui.phone.catalogue

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.databinding.FragmentPhoneCatalogueBinding
import com.lukakordzaia.streamflow.datamodels.VideoPlayerData
import com.lukakordzaia.streamflow.helpers.DotsIndicatorDecoration
import com.lukakordzaia.streamflow.network.LoadingState
import com.lukakordzaia.streamflow.ui.baseclasses.BaseVMFragment
import com.lukakordzaia.streamflow.ui.phone.videoplayer.VideoPlayerActivity
import com.lukakordzaia.streamflow.utils.*
import kotlinx.android.synthetic.main.main_top_toolbar.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class CatalogueFragment : BaseVMFragment<FragmentPhoneCatalogueBinding, CatalogueViewModel>() {
    override val viewModel by viewModel<CatalogueViewModel>()
    private lateinit var genresAdapter: GenresAdapter
    private lateinit var studiosAdapter: StudiosAdapter
    private lateinit var trailersAdapter: TrailersAdapter

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentPhoneCatalogueBinding
        get() = FragmentPhoneCatalogueBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        home_profile.setGone()

        fragmentObservers()
        trailersContainer()
        genresContainer()
        studiosContainer()
    }

    private fun fragmentObservers() {
        viewModel.noInternet.observe(viewLifecycleOwner, EventObserver {
            if (it) {
                requireContext().createToast(AppConstants.NO_INTERNET)
                Handler(Looper.getMainLooper()).postDelayed({
                    viewModel.refreshContent()
                }, 5000)
            }
        })
    }

    private fun trailersContainer() {
        viewModel.getTopTrailers()

        viewModel.trailersLoader.observe(viewLifecycleOwner, {
            when (it.status) {
                LoadingState.Status.RUNNING -> binding.trailersProgressBar.setVisible()
                LoadingState.Status.SUCCESS -> binding.trailersProgressBar.setGone()
            }
        })

        val trailerLayout = GridLayoutManager(requireActivity(), 1, GridLayoutManager.HORIZONTAL, false)
        trailersAdapter = TrailersAdapter(requireContext(),
            { titleId, trailerUrl ->
                startVideoPlayer(titleId, trailerUrl)
            },
            {
                viewModel.onSingleTrailerInfoPressed(it)
            }
        )
        binding.rvTrailers.layoutManager = trailerLayout
        binding.rvTrailers.adapter = trailersAdapter

        viewModel.topTrailerList.observe(viewLifecycleOwner, {
            trailersAdapter.setTrailerList(it)
        })

        val helper: SnapHelper = PagerSnapHelper()
        helper.attachToRecyclerView(binding.rvTrailers)

        val radius = resources.getDimensionPixelSize(R.dimen.radius);
        val dotsHeight = resources.getDimensionPixelSize(R.dimen.dots_height);
        val color = ContextCompat.getColor(requireContext(), R.color.general_text_color);

        binding.rvTrailers.addItemDecoration(DotsIndicatorDecoration(radius, radius * 4, dotsHeight, color, color))
    }

    private fun genresContainer() {
        viewModel.getAllGenres()

        viewModel.genresLoader.observe(viewLifecycleOwner, {
            when (it.status) {
                LoadingState.Status.RUNNING -> binding.genresProgressBar.setVisible()
                LoadingState.Status.SUCCESS -> binding.genresProgressBar.setGone()
            }
        })

        val genreLayout = GridLayoutManager(requireActivity(), 1, GridLayoutManager.HORIZONTAL, false)
        genresAdapter = GenresAdapter(requireContext()) { genreId: Int, genreName: String ->
            viewModel.onSingleGenrePressed(genreId, genreName)
        }
        binding.rvGenres.layoutManager = genreLayout
        binding.rvGenres.adapter = genresAdapter

        viewModel.allGenresList.observe(viewLifecycleOwner, {
            genresAdapter.setGenreList(it)
        })
    }

    private fun studiosContainer() {
        viewModel.getTopStudios()

        viewModel.studiosLoader.observe(viewLifecycleOwner, {
            when (it.status) {
                LoadingState.Status.RUNNING -> binding.studiosProgressBar.setVisible()
                LoadingState.Status.SUCCESS -> binding.studiosProgressBar.setGone()
            }
        })

        val studioLayout = GridLayoutManager(requireActivity(), 1, GridLayoutManager.HORIZONTAL, false)
        studiosAdapter = StudiosAdapter(requireContext()) { studioId: Int, studioName: String ->
            viewModel.onSingleStudioPressed(studioId, studioName)
        }
        binding.rvStudios.layoutManager = studioLayout
        binding.rvStudios.adapter = studiosAdapter

        viewModel.topGetTopStudiosResponse.observe(viewLifecycleOwner, {
            studiosAdapter.setStudioList(it)
        })
    }

    private fun startVideoPlayer(titleId: Int, trailerUrl: String?) {
        if (trailerUrl != null) {
            requireActivity().startActivity(VideoPlayerActivity.startFromTrailers(requireContext(), VideoPlayerData(
                titleId,
                false,
                0,
                "ENG",
                0,
                0L,
                trailerUrl
            )
            ))
        } else {
            viewModel.newToastMessage("ტრეილერი ვერ მოიძებნა")
        }
    }
}