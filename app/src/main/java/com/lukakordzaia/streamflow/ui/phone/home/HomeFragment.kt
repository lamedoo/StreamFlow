package com.lukakordzaia.streamflow.ui.phone.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.databinding.FragmentPhoneHomeBinding
import com.lukakordzaia.streamflow.datamodels.ContinueWatchingModel
import com.lukakordzaia.streamflow.datamodels.VideoPlayerData
import com.lukakordzaia.streamflow.network.LoadingState
import com.lukakordzaia.streamflow.ui.baseclasses.BaseFragment
import com.lukakordzaia.streamflow.ui.phone.home.homeadapters.HomeDbTitlesAdapter
import com.lukakordzaia.streamflow.ui.phone.home.homeadapters.HomeTitlesAdapter
import com.lukakordzaia.streamflow.ui.phone.videoplayer.VideoPlayerActivity
import com.lukakordzaia.streamflow.utils.*
import kotlinx.android.synthetic.main.main_top_toolbar.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class HomeFragment : BaseFragment<FragmentPhoneHomeBinding>() {
    private val homeViewModel: HomeViewModel by viewModel()

    private lateinit var homeDbTitlesAdapter: HomeDbTitlesAdapter
    private lateinit var homeNewMovieAdapter: HomeTitlesAdapter
    private lateinit var homeTopMovieAdapter: HomeTitlesAdapter
    private lateinit var homeTvShowAdapter: HomeTitlesAdapter

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentPhoneHomeBinding
        get() = FragmentPhoneHomeBinding::inflate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentListeners()
        fragmentObservers()

        movieDayContainer()
        continueWatchingContainer()
        newMoviesContainer()
        topMoviesContainer()
        topTvShowsContainer()
    }

    private fun fragmentListeners() {
        home_favorites.setOnClickListener {
            navController(HomeFragmentDirections.actionHomeFragmentToFavoritesFragment())
        }

        home_profile.setOnClickListener {
            navController(HomeFragmentDirections.actionHomeFragmentToProfileFragment())
        }

        binding.newMoviesHeader.setOnClickListener {
            homeViewModel.newMoviesMorePressed()
        }

        binding.topMoviesHeader.setOnClickListener {
            homeViewModel.topMoviesMorePressed()
        }

        binding.topTvShowsHeader.setOnClickListener {
            homeViewModel.topTvShowsMorePressed()
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
        homeViewModel.noInternet.observe(viewLifecycleOwner, EventObserver {
            if (it) {
                requireContext().createToast(AppConstants.NO_INTERNET)
                Handler(Looper.getMainLooper()).postDelayed({
                    homeViewModel.fetchContent(1)
                }, 5000)
            }
        })

        homeViewModel.navigateScreen.observe(viewLifecycleOwner, EventObserver {
            navController(it)
        })

        homeViewModel.toastMessage.observe(viewLifecycleOwner, EventObserver {
            requireContext().createToast(it)
        })
    }

    private fun movieDayContainer() {
        homeViewModel.movieDayLoader.observe(viewLifecycleOwner, {
            when (it.status) {
                LoadingState.Status.RUNNING -> binding.movieDayProgressBar.setVisible()
                LoadingState.Status.SUCCESS -> {
                    binding.movieDayProgressBar.setGone()
                    binding.movieDayContainer.setVisible()
                }
            }
        })

        homeViewModel.movieDayData.observe(viewLifecycleOwner, {
            binding.movieDayContainer.setOnClickListener { _ ->
                homeViewModel.onSingleTitlePressed(AppConstants.NAV_HOME_TO_SINGLE, it.first().id)
            }

            binding.movieDayName.text = it.first().displayName
            binding.movieDayCover.setImage(it.first().cover, false)
        })
    }

    private fun continueWatchingContainer() {
        homeViewModel.checkAuthDatabase()
        homeViewModel.contWatchingData.observe(viewLifecycleOwner, {
            homeViewModel.getContinueWatchingTitlesFromApi(it)
        })

        val dbLayout = GridLayoutManager(requireActivity(), 1, GridLayoutManager.HORIZONTAL, false)
        homeDbTitlesAdapter = HomeDbTitlesAdapter(requireContext(),
            {
                startVideoPlayer(it)
            },
            {
                homeViewModel.onSingleTitlePressed(AppConstants.NAV_HOME_TO_SINGLE, it)
            },
            { titleId: Int, titleName: String ->
                homeViewModel.onContinueWatchingInfoPressed(titleId, titleName)
            })
        binding.rvContinueWatchingTitles.adapter = homeDbTitlesAdapter
        binding.rvContinueWatchingTitles.layoutManager = dbLayout

        homeViewModel.continueWatchingList.observe(viewLifecycleOwner, {
            if (it.isNullOrEmpty()) {
                binding.continueWatchingContainer.setGone()
            } else {
                binding.continueWatchingContainer.setVisible()
                homeDbTitlesAdapter.setWatchedTitlesList(it)
            }
        })
    }

    private fun newMoviesContainer() {
        homeViewModel.newMovieLoader.observe(viewLifecycleOwner, {
            when (it.status) {
                LoadingState.Status.RUNNING -> binding.newMovieProgressBar.setVisible()
                LoadingState.Status.SUCCESS -> binding.newMovieProgressBar.setGone()
            }
        })

        val newMovieLayout = GridLayoutManager(requireActivity(), 1, GridLayoutManager.HORIZONTAL, false)
        homeNewMovieAdapter = HomeTitlesAdapter(requireContext()) {
            homeViewModel.onSingleTitlePressed(AppConstants.NAV_HOME_TO_SINGLE, it)
        }
        binding.rvNewMovies.adapter = homeNewMovieAdapter
        binding.rvNewMovies.layoutManager = newMovieLayout

        homeViewModel.newMovieList.observe(viewLifecycleOwner, {
            homeNewMovieAdapter.setItems(it)
        })
    }

    private fun topMoviesContainer() {
        homeViewModel.topMovieLoader.observe(viewLifecycleOwner, {
            when (it.status) {
                LoadingState.Status.RUNNING -> binding.topMovieProgressBar.setVisible()
                LoadingState.Status.SUCCESS -> binding.topMovieProgressBar.setGone()
            }
        })

        val topMovieLayout = GridLayoutManager(requireActivity(), 1, GridLayoutManager.HORIZONTAL, false)
        homeTopMovieAdapter = HomeTitlesAdapter(requireContext()) {
            homeViewModel.onSingleTitlePressed(AppConstants.NAV_HOME_TO_SINGLE, it)
        }
        binding.rvTopMovies.adapter = homeTopMovieAdapter
        binding.rvTopMovies.layoutManager = topMovieLayout

        homeViewModel.topMovieList.observe(viewLifecycleOwner, {
            homeTopMovieAdapter.setItems(it)
        })
    }

    private fun topTvShowsContainer() {
        homeViewModel.topTvShowsLoader.observe(viewLifecycleOwner, {
            when (it.status) {
                LoadingState.Status.RUNNING -> binding.topTvShowProgressBar.setVisible()
                LoadingState.Status.SUCCESS -> binding.topTvShowProgressBar.setGone()
            }
        })

        val tvShowLayout = GridLayoutManager(requireActivity(), 1, GridLayoutManager.HORIZONTAL, false)
        homeTvShowAdapter = HomeTitlesAdapter(requireContext()) {
            homeViewModel.onSingleTitlePressed(AppConstants.NAV_HOME_TO_SINGLE, it)
        }
        binding.rvTopTvShows.adapter = homeTvShowAdapter
        binding.rvTopTvShows.layoutManager = tvShowLayout

        homeViewModel.topTvShowList.observe(viewLifecycleOwner, {
            homeTvShowAdapter.setItems(it)
        })
    }

    private fun startVideoPlayer(data: ContinueWatchingModel) {
        requireActivity().startActivity(VideoPlayerActivity.startFromHomeScreen(requireContext(), VideoPlayerData(
            data.id,
            data.isTvShow,
            data.season,
            data.language,
            data.episode,
            data.watchedDuration,
            null
        )
        ))
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_fragment_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.profile_button -> {
                navController(HomeFragmentDirections.actionHomeFragmentToProfileFragment())
            }
            R.id.favorite_button -> {
                navController(HomeFragmentDirections.actionHomeFragmentToFavoritesFragment())
            }
        }
        return super.onOptionsItemSelected(item)
    }
}