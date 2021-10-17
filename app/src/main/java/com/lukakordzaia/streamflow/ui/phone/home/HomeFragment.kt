package com.lukakordzaia.streamflow.ui.phone.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.databinding.FragmentPhoneHomeBinding
import com.lukakordzaia.streamflow.datamodels.ContinueWatchingModel
import com.lukakordzaia.streamflow.datamodels.VideoPlayerData
import com.lukakordzaia.streamflow.network.LoadingState
import com.lukakordzaia.streamflow.ui.baseclasses.BaseVMFragment
import com.lukakordzaia.streamflow.ui.phone.home.homeadapters.HomeDbTitlesAdapter
import com.lukakordzaia.streamflow.ui.phone.home.homeadapters.HomeTitlesAdapter
import com.lukakordzaia.streamflow.ui.phone.videoplayer.VideoPlayerActivity
import com.lukakordzaia.streamflow.utils.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit


class HomeFragment : BaseVMFragment<FragmentPhoneHomeBinding, HomeViewModel>() {
    override val viewModel by viewModel<HomeViewModel>()

    private lateinit var homeDbTitlesAdapter: HomeDbTitlesAdapter
    private lateinit var homeNewMovieAdapter: HomeTitlesAdapter
    private lateinit var homeTopMovieAdapter: HomeTitlesAdapter
    private lateinit var homeTvShowAdapter: HomeTitlesAdapter

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentPhoneHomeBinding
        get() = FragmentPhoneHomeBinding::inflate

    override fun onStart() {
        super.onStart()
        if (sharedPreferences.getTvVideoPlayerOn()) {
            continueWatchingContainer()
            sharedPreferences.saveTvVideoPlayerOn(false)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentListeners()
        fragmentObservers()

        continueWatchingContainer()
        movieDayContainer()
        newMoviesContainer()
        topMoviesContainer()
        topTvShowsContainer()
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
        viewModel.noInternet.observe(viewLifecycleOwner, EventObserver {
            if (it) {
                requireContext().createToast(AppConstants.NO_INTERNET)
                Handler(Looper.getMainLooper()).postDelayed({
                    viewModel.fetchContent(1)
                }, 5000)
            }
        })
    }

    private fun movieDayContainer() {
        viewModel.movieDayLoader.observe(viewLifecycleOwner, {
            when (it.status) {
                LoadingState.Status.RUNNING -> binding.movieDayProgressBar.setVisible()
                LoadingState.Status.SUCCESS -> {
                    binding.movieDayProgressBar.setGone()
                    binding.movieDayContainer.setVisible()
                }
            }
        })

        viewModel.movieDayData.observe(viewLifecycleOwner, {
            binding.movieDayContainer.setOnClickListener { _ ->
                viewModel.onSingleTitlePressed(AppConstants.NAV_HOME_TO_SINGLE, it.first().id)
            }

            binding.movieDayName.text = it.first().displayName
            binding.movieDayCover.setImage(it.first().cover, false)
        })
    }

    private fun continueWatchingContainer() {
        viewModel.continueWatchingLoader.observe(viewLifecycleOwner, {
            when (it.status) {
                LoadingState.Status.RUNNING -> {
                    binding.continueWatchingContainer.setGone()
                }
                LoadingState.Status.SUCCESS -> {
                    binding.continueWatchingContainer.setVisible()
                }
            }
        })

        viewModel.checkAuthDatabase()
        viewModel.contWatchingData.observe(viewLifecycleOwner, {
            viewModel.getContinueWatchingTitlesFromApi(it)
        })

        val dbLayout = GridLayoutManager(requireActivity(), 1, GridLayoutManager.HORIZONTAL, false)
        homeDbTitlesAdapter = HomeDbTitlesAdapter(requireContext(),
            {
                startVideoPlayer(it)
            },
            {
                viewModel.onSingleTitlePressed(AppConstants.NAV_HOME_TO_SINGLE, it)
            },
            { titleId: Int, titleName: String ->
                viewModel.onContinueWatchingInfoPressed(titleId, titleName)
            })
        binding.rvContinueWatchingTitles.adapter = homeDbTitlesAdapter
        binding.rvContinueWatchingTitles.layoutManager = dbLayout

        viewModel.continueWatchingList.observe(viewLifecycleOwner, {
            if (it.isNullOrEmpty()) {
                binding.continueWatchingContainer.setGone()
            } else {
                binding.continueWatchingContainer.setVisible()
                homeDbTitlesAdapter.setWatchedTitlesList(it)
            }
        })
    }

    private fun newMoviesContainer() {
        viewModel.newMovieLoader.observe(viewLifecycleOwner, {
            when (it.status) {
                LoadingState.Status.RUNNING -> binding.newMovieProgressBar.setVisible()
                LoadingState.Status.SUCCESS -> binding.newMovieProgressBar.setGone()
            }
        })

        val newMovieLayout = GridLayoutManager(requireActivity(), 1, GridLayoutManager.HORIZONTAL, false)
        homeNewMovieAdapter = HomeTitlesAdapter(requireContext()) {
            viewModel.onSingleTitlePressed(AppConstants.NAV_HOME_TO_SINGLE, it)
        }
        binding.rvNewMovies.adapter = homeNewMovieAdapter
        binding.rvNewMovies.layoutManager = newMovieLayout

        viewModel.newMovieList.observe(viewLifecycleOwner, {
            homeNewMovieAdapter.setItems(it)
        })
    }

    private fun topMoviesContainer() {
        viewModel.topMovieLoader.observe(viewLifecycleOwner, {
            when (it.status) {
                LoadingState.Status.RUNNING -> binding.topMovieProgressBar.setVisible()
                LoadingState.Status.SUCCESS -> binding.topMovieProgressBar.setGone()
            }
        })

        val topMovieLayout = GridLayoutManager(requireActivity(), 1, GridLayoutManager.HORIZONTAL, false)
        homeTopMovieAdapter = HomeTitlesAdapter(requireContext()) {
            viewModel.onSingleTitlePressed(AppConstants.NAV_HOME_TO_SINGLE, it)
        }
        binding.rvTopMovies.adapter = homeTopMovieAdapter
        binding.rvTopMovies.layoutManager = topMovieLayout

        viewModel.topMovieList.observe(viewLifecycleOwner, {
            homeTopMovieAdapter.setItems(it)
        })
    }

    private fun topTvShowsContainer() {
        viewModel.topTvShowsLoader.observe(viewLifecycleOwner, {
            when (it.status) {
                LoadingState.Status.RUNNING -> binding.topTvShowProgressBar.setVisible()
                LoadingState.Status.SUCCESS -> binding.topTvShowProgressBar.setGone()
            }
        })

        val tvShowLayout = GridLayoutManager(requireActivity(), 1, GridLayoutManager.HORIZONTAL, false)
        homeTvShowAdapter = HomeTitlesAdapter(requireContext()) {
            viewModel.onSingleTitlePressed(AppConstants.NAV_HOME_TO_SINGLE, it)
        }
        binding.rvTopTvShows.adapter = homeTvShowAdapter
        binding.rvTopTvShows.layoutManager = tvShowLayout

        viewModel.topTvShowList.observe(viewLifecycleOwner, {
            homeTvShowAdapter.setItems(it)
        })
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