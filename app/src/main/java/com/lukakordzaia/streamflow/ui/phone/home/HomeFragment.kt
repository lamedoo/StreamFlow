package com.lukakordzaia.streamflow.ui.phone.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.databinding.FragmentPhoneHomeBinding
import com.lukakordzaia.streamflow.datamodels.ContinueWatchingModel
import com.lukakordzaia.streamflow.datamodels.SingleTitleModel
import com.lukakordzaia.streamflow.datamodels.VideoPlayerData
import com.lukakordzaia.streamflow.network.LoadingState
import com.lukakordzaia.streamflow.ui.baseclasses.fragments.BaseFragmentPhoneVM
import com.lukakordzaia.streamflow.ui.phone.home.homeadapters.HomeContinueWatchingAdapter
import com.lukakordzaia.streamflow.ui.phone.home.homeadapters.HomeNewSeriesAdapter
import com.lukakordzaia.streamflow.ui.phone.home.homeadapters.HomeTitlesAdapter
import com.lukakordzaia.streamflow.ui.phone.videoplayer.VideoPlayerActivity
import com.lukakordzaia.streamflow.utils.AppConstants
import com.lukakordzaia.streamflow.utils.setImage
import com.lukakordzaia.streamflow.utils.setVisibleOrGone
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit


class HomeFragment : BaseFragmentPhoneVM<FragmentPhoneHomeBinding, HomeViewModel>() {
    override val viewModel by viewModel<HomeViewModel>()
    override val reload: () -> Unit = { viewModel.fetchContent(1) }

    private lateinit var homeContinueWatchingAdapter: HomeContinueWatchingAdapter
    private lateinit var homeNewMovieAdapter: HomeTitlesAdapter
    private lateinit var homeTopMovieAdapter: HomeTitlesAdapter
    private lateinit var homeTvShowAdapter: HomeTitlesAdapter
    private lateinit var homeNewSeriesAdapter: HomeNewSeriesAdapter

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentPhoneHomeBinding
        get() = FragmentPhoneHomeBinding::inflate

    override fun onStart() {
        super.onStart()
        if (sharedPreferences.getTvVideoPlayerOn()) {
            viewModel.checkAuthDatabase()
            sharedPreferences.saveTvVideoPlayerOn(false)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.checkAuthDatabase()

        fragmentSetUi()
        fragmentListeners()
        fragmentObservers()
    }

    private fun fragmentSetUi() {
        continueWatchingContainer()
        newMoviesContainer()
        topMoviesContainer()
        topTvShowsContainer()
        newSeriesContainer()
    }

    private fun fragmentListeners() {
        binding.toolbar.homeProfile.setOnClickListener {
            viewModel.onProfilePressed()
        }

        binding.newMoviesHeader.setOnClickListener {
            viewModel.onTopListPressed(AppConstants.LIST_NEW_MOVIES)
        }

        binding.topMoviesHeader.setOnClickListener {
            viewModel.onTopListPressed(AppConstants.LIST_TOP_MOVIES)
        }

        binding.topTvShowsHeader.setOnClickListener {
            viewModel.onTopListPressed(AppConstants.LIST_TOP_TV_SHOWS)
        }

        binding.fragmentScroll.setOnScrollChangeListener { _: View, _: Int, scrollY: Int, _: Int, _: Int ->
            if (scrollY > 0) {
                binding.toolbar.root.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.primaryColor, null))
                binding.toolbar.root.background.alpha = 255
            } else {
                binding.toolbar.root.background.alpha = 0
            }
        }
    }

    private fun fragmentObservers() {
        viewModel.generalLoader.observe(viewLifecycleOwner, {
            binding.generalProgressBar.setVisibleOrGone(it == LoadingState.LOADING)
            binding.fragmentScroll.setVisibleOrGone(it != LoadingState.LOADING)
        })

        viewModel.continueWatchingLoader.observe(viewLifecycleOwner, {
            binding.continueWatchingProgressBar.setVisibleOrGone(it == LoadingState.LOADING)
        })

        viewModel.movieDayData.observe(viewLifecycleOwner, {
            movieDayContainer(it.first())
        })

        //is needed if user is not signed in and titles are saved in room
        viewModel.contWatchingData.observe(viewLifecycleOwner, {
            viewModel.getContinueWatchingTitlesFromApi(it)
        })

        viewModel.continueWatchingList.observe(viewLifecycleOwner, {
            homeContinueWatchingAdapter.setWatchedTitlesList(it)
        })

        viewModel.newMovieList.observe(viewLifecycleOwner, {
            homeNewMovieAdapter.setItems(it)
        })

        viewModel.topMovieList.observe(viewLifecycleOwner, {
            homeTopMovieAdapter.setItems(it)
        })

        viewModel.topTvShowList.observe(viewLifecycleOwner, {
            homeTvShowAdapter.setItems(it)
        })

        viewModel.newSeriesList.observe(viewLifecycleOwner, {
            homeNewSeriesAdapter.setItems(it)
        })
    }

    private fun movieDayContainer(movie: SingleTitleModel) {
        binding.movieDayContainer.setOnClickListener {
            viewModel.onSingleTitlePressed(AppConstants.NAV_HOME_TO_SINGLE, movie.id)
        }

        binding.movieDayName.text = movie.displayName
        binding.movieDayCover.setImage(movie.cover, false)
    }

    private fun continueWatchingContainer() {
        val dbLayout = GridLayoutManager(requireActivity(), 1, GridLayoutManager.HORIZONTAL, false)
        homeContinueWatchingAdapter = HomeContinueWatchingAdapter(requireContext(),
            {
                startVideoPlayer(it)
            },
            {
                viewModel.onSingleTitlePressed(AppConstants.NAV_HOME_TO_SINGLE, it)
            },
            { titleId: Int, titleName: String ->
                viewModel.onContinueWatchingInfoPressed(titleId, titleName)
            })
        binding.continueWatchingTitles.apply {
            adapter = homeContinueWatchingAdapter
            layoutManager = dbLayout
        }
    }

    private fun newMoviesContainer() {
        val newMovieLayout = GridLayoutManager(requireActivity(), 1, GridLayoutManager.HORIZONTAL, false)
        homeNewMovieAdapter = HomeTitlesAdapter(requireContext()) {
            viewModel.onSingleTitlePressed(AppConstants.NAV_HOME_TO_SINGLE, it)
        }
        binding.rvNewMovies.apply {
            adapter = homeNewMovieAdapter
            layoutManager = newMovieLayout
        }
    }

    private fun topMoviesContainer() {
        val topMovieLayout = GridLayoutManager(requireActivity(), 1, GridLayoutManager.HORIZONTAL, false)
        homeTopMovieAdapter = HomeTitlesAdapter(requireContext()) {
            viewModel.onSingleTitlePressed(AppConstants.NAV_HOME_TO_SINGLE, it)
        }
        binding.rvTopMovies.apply {
            adapter = homeTopMovieAdapter
            layoutManager = topMovieLayout
        }
    }

    private fun topTvShowsContainer() {
        val tvShowLayout = GridLayoutManager(requireActivity(), 1, GridLayoutManager.HORIZONTAL, false)
        homeTvShowAdapter = HomeTitlesAdapter(requireContext()) {
            viewModel.onSingleTitlePressed(AppConstants.NAV_HOME_TO_SINGLE, it)
        }
        binding.rvTopTvShows.apply {
            adapter = homeTvShowAdapter
            layoutManager = tvShowLayout
        }
    }

    private fun newSeriesContainer() {
        val newSeriesLayout = GridLayoutManager(requireActivity(), 1, GridLayoutManager.HORIZONTAL, false)
        homeNewSeriesAdapter = HomeNewSeriesAdapter(requireActivity()) {
            viewModel.onSingleTitlePressed(AppConstants.NAV_HOME_TO_SINGLE, it)
        }
        binding.rvNewSeries.apply {
            adapter = homeNewSeriesAdapter
            layoutManager = newSeriesLayout
        }
    }

    private fun startVideoPlayer(data: ContinueWatchingModel) {
        requireActivity().startActivity(VideoPlayerActivity.startFromHomeScreen(requireContext(), VideoPlayerData(
            data.id,
            data.isTvShow,
            if (data.isTvShow) data.season else 0,
            data.language,
            if (data.isTvShow) data.episode else 0,
            TimeUnit.SECONDS.toMillis(data.watchedDuration),
            null
        )
        ))
    }
}