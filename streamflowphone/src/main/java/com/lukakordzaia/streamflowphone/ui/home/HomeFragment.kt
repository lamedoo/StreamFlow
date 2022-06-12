package com.lukakordzaia.streamflowphone.ui.home

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.lukakordzaia.core.domain.domainmodels.ContinueWatchingModel
import com.lukakordzaia.core.domain.domainmodels.GithubReleaseModel
import com.lukakordzaia.core.domain.domainmodels.SingleTitleModel
import com.lukakordzaia.core.domain.domainmodels.VideoPlayerData
import com.lukakordzaia.core.network.LoadingState
import com.lukakordzaia.core.utils.*
import com.lukakordzaia.streamflowphone.R
import com.lukakordzaia.streamflowphone.databinding.FragmentPhoneHomeBinding
import com.lukakordzaia.streamflowphone.ui.baseclasses.BaseFragmentPhoneVM
import com.lukakordzaia.streamflowphone.ui.home.homeadapters.HomeContinueWatchingAdapter
import com.lukakordzaia.streamflowphone.ui.home.homeadapters.HomeNewSeriesAdapter
import com.lukakordzaia.streamflowphone.ui.home.homeadapters.HomeTitlesAdapter
import com.lukakordzaia.streamflowphone.ui.videoplayer.VideoPlayerActivity
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.io.File
import java.util.concurrent.TimeUnit


class HomeFragment : BaseFragmentPhoneVM<FragmentPhoneHomeBinding, HomeViewModel>() {
    override val viewModel by sharedViewModel<HomeViewModel>()
    override val reload: () -> Unit = { viewModel.fetchContent(1) }

    private lateinit var homeContinueWatchingAdapter: HomeContinueWatchingAdapter
    private lateinit var homeNewMovieAdapter: HomeTitlesAdapter
    private lateinit var homeTopMovieAdapter: HomeTitlesAdapter
    private lateinit var homeTvShowAdapter: HomeTitlesAdapter
    private lateinit var homeNewSeriesAdapter: HomeNewSeriesAdapter
    private lateinit var homeUserSuggestionsAdapter: HomeTitlesAdapter

    private lateinit var downloadReleaseDialog: Dialog

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentPhoneHomeBinding
        get() = FragmentPhoneHomeBinding::inflate

    override fun onStart() {
        super.onStart()
        if (sharedPreferences.getRefreshContinueWatching()) {
            viewModel.checkAuthDatabase()
            sharedPreferences.saveRefreshContinueWatching(false)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.checkAuthDatabase()

        viewModel.checkGithubReleases(
            getString(R.string.version_number_check, requireContext().packageManager.getPackageInfo(requireContext().packageName, 0).versionName)
        )

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
        userSuggestionsContainer()
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
        viewModel.generalLoader.observe(viewLifecycleOwner) {
            binding.generalProgressBar.setVisibleOrGone(it == LoadingState.LOADING)
            binding.fragmentScroll.setVisibleOrGone(it != LoadingState.LOADING)
        }

        viewModel.continueWatchingLoader.observe(viewLifecycleOwner) {
            binding.continueWatchingProgressBar.setVisibleOrGone(it == LoadingState.LOADING)
        }

        viewModel.movieDayData.observe(viewLifecycleOwner) {
            movieDayContainer(it.first())
        }

        viewModel.continueWatchingList.observe(viewLifecycleOwner) {
            binding.continueWatchingContainer.setVisibleOrGone(!it.isNullOrEmpty())
            homeContinueWatchingAdapter.setWatchedTitlesList(it)
        }

        viewModel.newMovieList.observe(viewLifecycleOwner) {
            homeNewMovieAdapter.setItems(it)
        }

        viewModel.topMovieList.observe(viewLifecycleOwner) {
            homeTopMovieAdapter.setItems(it)
        }

        viewModel.topTvShowList.observe(viewLifecycleOwner) {
            homeTvShowAdapter.setItems(it)
        }

        viewModel.newSeriesList.observe(viewLifecycleOwner) {
            homeNewSeriesAdapter.setItems(it)
        }

        viewModel.userSuggestionsList.observe(viewLifecycleOwner) {
            homeUserSuggestionsAdapter.setItems(it)
        }

        viewModel.hideContinueWatchingLoader.observe(viewLifecycleOwner) {
            if (it == LoadingState.LOADED) {
                viewModel.checkAuthDatabase()
            }
        }

        viewModel.releaseUrl.observe(viewLifecycleOwner, EventObserver {
            downloadReleaseDialog(it)
        })
    }

    private fun movieDayContainer(movie: SingleTitleModel) {
        binding.movieDay.movieDayMore.setOnClickListener {
            viewModel.onSingleTitlePressed(AppConstants.NAV_HOME_TO_SINGLE, movie.id)
        }

        binding.movieDay.playButton.setOnClickListener {
            viewModel.onSingleTitlePressed(AppConstants.NAV_HOME_TO_SINGLE, movie.id)
        }

        binding.movieDay.movieDayName.text = movie.displayName
        binding.movieDay.movieDayCover.setImage(movie.cover, false)
    }

    private fun continueWatchingContainer() {
        val dbLayout = GridLayoutManager(requireActivity(), 1, GridLayoutManager.HORIZONTAL, false)
        homeContinueWatchingAdapter = HomeContinueWatchingAdapter(
            {
                startVideoPlayer(it)
            },
            { titleId: Int, titleName: String ->
                viewModel.onContinueWatchingInfoPressed(titleId, titleName)
            })
        binding.rvContinueWatching.apply {
            adapter = homeContinueWatchingAdapter
            layoutManager = dbLayout
        }
    }

    private fun newMoviesContainer() {
        val newMovieLayout = GridLayoutManager(requireActivity(), 1, GridLayoutManager.HORIZONTAL, false)
        homeNewMovieAdapter = HomeTitlesAdapter {
            viewModel.onSingleTitlePressed(AppConstants.NAV_HOME_TO_SINGLE, it)
        }
        binding.rvNewMovies.apply {
            adapter = homeNewMovieAdapter
            layoutManager = newMovieLayout
        }
    }

    private fun topMoviesContainer() {
        val topMovieLayout = GridLayoutManager(requireActivity(), 1, GridLayoutManager.HORIZONTAL, false)
        homeTopMovieAdapter = HomeTitlesAdapter {
            viewModel.onSingleTitlePressed(AppConstants.NAV_HOME_TO_SINGLE, it)
        }
        binding.rvTopMovies.apply {
            adapter = homeTopMovieAdapter
            layoutManager = topMovieLayout
        }
    }

    private fun topTvShowsContainer() {
        val tvShowLayout = GridLayoutManager(requireActivity(), 1, GridLayoutManager.HORIZONTAL, false)
        homeTvShowAdapter = HomeTitlesAdapter {
            viewModel.onSingleTitlePressed(AppConstants.NAV_HOME_TO_SINGLE, it)
        }
        binding.rvTopTvShows.apply {
            adapter = homeTvShowAdapter
            layoutManager = tvShowLayout
        }
    }

    private fun newSeriesContainer() {
        val newSeriesLayout = GridLayoutManager(requireActivity(), 1, GridLayoutManager.HORIZONTAL, false)
        homeNewSeriesAdapter = HomeNewSeriesAdapter {
            viewModel.onSingleTitlePressed(AppConstants.NAV_HOME_TO_SINGLE, it)
        }
        binding.rvNewSeries.apply {
            adapter = homeNewSeriesAdapter
            layoutManager = newSeriesLayout
        }
    }

    private fun userSuggestionsContainer() {
        binding.userSuggestionsContainer.setVisibleOrGone(sharedPreferences.getLoginToken() != "")

        val suggestionsLayout = GridLayoutManager(requireActivity(), 1, GridLayoutManager.HORIZONTAL, false)
        homeUserSuggestionsAdapter = HomeTitlesAdapter {
            viewModel.onSingleTitlePressed(AppConstants.NAV_HOME_TO_SINGLE, it)
        }
        binding.rvUserSuggestion.apply {
            adapter = homeUserSuggestionsAdapter
            layoutManager = suggestionsLayout
        }
    }

    private fun startVideoPlayer(data: ContinueWatchingModel) {
        requireActivity().startActivity(
            VideoPlayerActivity.startFromHomeScreen(requireContext(), VideoPlayerData(
                data.id,
                data.isTvShow,
                if (data.isTvShow) data.season else 0,
                data.language,
                if (data.isTvShow) data.episode else 0,
                TimeUnit.SECONDS.toMillis(data.watchedDuration),
                null
            )
            )
        )
    }

    private fun downloadReleaseDialog(release: GithubReleaseModel) {
        downloadReleaseDialog = DialogUtils.downloadReleaseAlertDialog(requireContext()) {
            viewModel.downloadNewRelease(release.downloadUrl!!, "streamflow-${release.tag}.apk", requireContext()) { uri ->
                uri?.let {
                    downloadReleaseDialog.dismiss()
                    startPackageActivity(this, File(it)) {
                        viewModel.newToastMessage(getString(R.string.no_activity_found))
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        with(binding) {
            rvContinueWatching.adapter = null
            rvNewMovies.adapter = null
            rvTopMovies.adapter = null
            rvUserSuggestion.adapter = null
            rvTopTvShows.adapter = null
            rvNewSeries.adapter = null
        }
        super.onDestroyView()
    }
}