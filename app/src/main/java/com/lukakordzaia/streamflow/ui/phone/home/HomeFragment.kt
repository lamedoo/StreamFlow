package com.lukakordzaia.streamflow.ui.phone.home

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import androidx.recyclerview.widget.GridLayoutManager
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.databinding.FragmentPhoneHomeBinding
import com.lukakordzaia.streamflow.datamodels.VideoPlayerData
import com.lukakordzaia.streamflow.network.LoadingState
import com.lukakordzaia.streamflow.ui.baseclasses.BaseFragment
import com.lukakordzaia.streamflow.ui.phone.home.homeadapters.HomeDbTitlesAdapter
import com.lukakordzaia.streamflow.ui.phone.home.homeadapters.HomeNewMovieAdapter
import com.lukakordzaia.streamflow.ui.phone.home.homeadapters.HomeTopMovieAdapter
import com.lukakordzaia.streamflow.ui.phone.home.homeadapters.HomeTvShowAdapter
import com.lukakordzaia.streamflow.ui.phone.videoplayer.VideoPlayerActivity
import com.lukakordzaia.streamflow.utils.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.main_top_toolbar.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class HomeFragment : BaseFragment<FragmentPhoneHomeBinding>() {
    private val homeViewModel: HomeViewModel by viewModel()

    private lateinit var homeDbTitlesAdapter: HomeDbTitlesAdapter
    private lateinit var homeNewMovieAdapter: HomeNewMovieAdapter
    private lateinit var homeTopMovieAdapter: HomeTopMovieAdapter
    private lateinit var homeTvShowAdapter: HomeTvShowAdapter

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentPhoneHomeBinding
        get() = FragmentPhoneHomeBinding::inflate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!hasInitializedRootView) {
            hasInitializedRootView = true
        }

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

        binding.fragmentScroll.setOnScrollChangeListener { v: View, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
            if (scrollY > 0) {
                binding.toolbar.root.setBackgroundColor(Color.parseColor("#282A38"))
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
                    homeViewModel.refreshContent(1)
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
                homeViewModel.onSingleTitlePressed(AppConstants.NAV_HOME_TO_SINGLE, it.id)
            }

            Picasso.get().load(it.covers?.data?.x1050).placeholder(R.drawable.movie_image_placeholder).error(R.drawable.movie_image_placeholder).into(binding.movieDayCover)

            if (it.primaryName.isNotEmpty()) {
                binding.movieDayName.text = it.primaryName
            } else {
                binding.movieDayName.text = it.secondaryName
            }
        })
    }

    private fun continueWatchingContainer() {
        val dbLayout = GridLayoutManager(requireActivity(), 1, GridLayoutManager.HORIZONTAL, false)
        homeDbTitlesAdapter = HomeDbTitlesAdapter(requireContext(),
            {
                val intent = Intent(context, VideoPlayerActivity::class.java)
                intent.putExtra("videoPlayerData", VideoPlayerData(
                    it.id,
                    it.isTvShow,
                    it.season,
                    it.language,
                    it.episode,
                    it.watchedDuration,
                    null
                ))
                activity?.startActivity(intent)
            },
            {
                homeViewModel.onSingleTitlePressed(AppConstants.NAV_HOME_TO_SINGLE, it)
            },
            { titleId: Int, titleName: String ->
                homeViewModel.onContinueWatchingInfoPressed(titleId, titleName)
            })
        binding.rvContinueWatchingTitles.adapter = homeDbTitlesAdapter
        binding.rvContinueWatchingTitles.layoutManager = dbLayout

        if (requireActivity().resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (auth.currentUser == null) {
                homeViewModel.getContinueWatchingFromRoom(requireContext()).observe(viewLifecycleOwner, {
                    homeViewModel.getContinueWatchingTitlesFromApi(it)
                })
            } else {
                homeViewModel.getContinueWatchingFromFirestore()
            }
        }

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

        binding.newMoviesMore.setOnClickListener {
            homeViewModel.newMoviesMorePressed()
        }

        val newMovieLayout = GridLayoutManager(requireActivity(), 1, GridLayoutManager.HORIZONTAL, false)
        homeNewMovieAdapter = HomeNewMovieAdapter(requireContext()) {
            homeViewModel.onSingleTitlePressed(AppConstants.NAV_HOME_TO_SINGLE, it)
        }
        binding.rvNewMovies.adapter = homeNewMovieAdapter
        binding.rvNewMovies.layoutManager = newMovieLayout

        homeViewModel.newMovieList.observe(viewLifecycleOwner, {
            homeNewMovieAdapter.setMoviesList(it)
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
        homeTopMovieAdapter = HomeTopMovieAdapter(requireContext()) {
            homeViewModel.onSingleTitlePressed(AppConstants.NAV_HOME_TO_SINGLE, it)
        }
        binding.rvTopMovies.adapter = homeTopMovieAdapter
        binding.rvTopMovies.layoutManager = topMovieLayout

        homeViewModel.topMovieList.observe(viewLifecycleOwner, {
            homeTopMovieAdapter.setMoviesList(it)
        })

        binding.topMoviesMore.setOnClickListener {
            homeViewModel.topMoviesMorePressed()
        }
    }

    private fun topTvShowsContainer() {
        homeViewModel.topTvShowsLoader.observe(viewLifecycleOwner, {
            when (it.status) {
                LoadingState.Status.RUNNING -> binding.topTvShowProgressBar.setVisible()
                LoadingState.Status.SUCCESS -> binding.topTvShowProgressBar.setGone()
            }
        })

        val tvShowLayout = GridLayoutManager(requireActivity(), 1, GridLayoutManager.HORIZONTAL, false)
        homeTvShowAdapter = HomeTvShowAdapter(requireContext()) {
            homeViewModel.onSingleTitlePressed(AppConstants.NAV_HOME_TO_SINGLE, it)
        }
        binding.rvTopTvShows.adapter = homeTvShowAdapter
        binding.rvTopTvShows.layoutManager = tvShowLayout

        homeViewModel.topTvShowList.observe(viewLifecycleOwner, {
            homeTvShowAdapter.setTvShowsList(it)
        })

        binding.topTvShowsMore.setOnClickListener {
            homeViewModel.topTvShowsMorePressed()
        }
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