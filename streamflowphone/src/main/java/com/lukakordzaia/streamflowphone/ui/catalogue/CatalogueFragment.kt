package com.lukakordzaia.streamflowphone.ui.catalogue

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.lukakordzaia.core.datamodels.VideoPlayerData
import com.lukakordzaia.core.network.LoadingState
import com.lukakordzaia.core.utils.setGone
import com.lukakordzaia.core.utils.setVisibleOrGone
import com.lukakordzaia.streamflowphone.R
import com.lukakordzaia.streamflowphone.databinding.FragmentPhoneCatalogueBinding
import com.lukakordzaia.streamflowphone.helpers.DotsIndicatorDecoration
import com.lukakordzaia.streamflowphone.ui.baseclasses.BaseFragmentPhoneVM
import com.lukakordzaia.streamflowphone.ui.catalogue.catalogueadapters.GenresAdapter
import com.lukakordzaia.streamflowphone.ui.catalogue.catalogueadapters.StudiosAdapter
import com.lukakordzaia.streamflowphone.ui.catalogue.catalogueadapters.TrailersAdapter
import com.lukakordzaia.streamflowphone.ui.videoplayer.VideoPlayerActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class CatalogueFragment : BaseFragmentPhoneVM<FragmentPhoneCatalogueBinding, CatalogueViewModel>() {
    override val viewModel by viewModel<CatalogueViewModel>()
    override val reload: () -> Unit = { viewModel.fetchContent() }
    private lateinit var genresAdapter: GenresAdapter
    private lateinit var studiosAdapter: StudiosAdapter
    private lateinit var trailersAdapter: TrailersAdapter

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentPhoneCatalogueBinding
        get() = FragmentPhoneCatalogueBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentSetUi()
        fragmentObservers()
    }

    private fun fragmentSetUi() {
        requireActivity().findViewById<ImageView>(R.id.home_profile).setGone()
        trailersContainer()
        genresContainer()
        studiosContainer()
    }

    private fun fragmentObservers() {
        viewModel.generalLoader.observe(viewLifecycleOwner, {
            binding.generalProgressBar.setVisibleOrGone(it == LoadingState.LOADING)
            binding.fragmentScroll.setVisibleOrGone(it != LoadingState.LOADING)
        })

        viewModel.topTrailerList.observe(viewLifecycleOwner, {
            trailersAdapter.setTrailerList(it)
        })

        viewModel.allGenresList.observe(viewLifecycleOwner, {
            genresAdapter.setGenreList(it)
        })

        viewModel.topGetTopStudiosResponse.observe(viewLifecycleOwner, {
            studiosAdapter.setStudioList(it)
        })
    }

    private fun trailersContainer() {
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

        val helper: SnapHelper = PagerSnapHelper()
        helper.attachToRecyclerView(binding.rvTrailers)

        val radius = resources.getDimensionPixelSize(R.dimen.radius)
        val dotsHeight = resources.getDimensionPixelSize(R.dimen.dots_height)
        val color = ContextCompat.getColor(requireContext(), R.color.general_text_color)

        binding.rvTrailers.addItemDecoration(DotsIndicatorDecoration(radius, radius * 4, dotsHeight, color, color))
    }

    private fun genresContainer() {
        val genreLayout = GridLayoutManager(requireActivity(), 1, GridLayoutManager.HORIZONTAL, false)
        genresAdapter = GenresAdapter(requireContext()) { genreId: Int, genreName: String ->
            viewModel.onSingleGenrePressed(genreId, genreName)
        }
        binding.rvGenres.layoutManager = genreLayout
        binding.rvGenres.adapter = genresAdapter
    }

    private fun studiosContainer() {
        val studioLayout = GridLayoutManager(requireActivity(), 1, GridLayoutManager.HORIZONTAL, false)
        studiosAdapter = StudiosAdapter(requireContext()) { studioId: Int, studioName: String ->
            viewModel.onSingleStudioPressed(studioId, studioName)
        }
        binding.rvStudios.layoutManager = studioLayout
        binding.rvStudios.adapter = studiosAdapter
    }

    private fun startVideoPlayer(titleId: Int, trailerUrl: String?) {
        if (trailerUrl != null) {
            requireActivity().startActivity(
                VideoPlayerActivity.startFromTrailers(requireContext(), VideoPlayerData(
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
            viewModel.newToastMessage(getString(R.string.no_trailer_found))
        }
    }
}