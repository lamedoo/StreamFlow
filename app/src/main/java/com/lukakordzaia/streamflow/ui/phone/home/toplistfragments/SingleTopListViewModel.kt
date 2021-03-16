package com.lukakordzaia.streamflow.ui.phone.home.toplistfragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lukakordzaia.streamflow.datamodels.TitleList
import com.lukakordzaia.streamflow.network.LoadingState
import com.lukakordzaia.streamflow.network.Result
import com.lukakordzaia.streamflow.repository.HomeRepository
import com.lukakordzaia.streamflow.ui.baseclasses.BaseViewModel
import com.lukakordzaia.streamflow.utils.AppConstants
import kotlinx.coroutines.launch

class SingleTopListViewModel(private val repository: HomeRepository) : BaseViewModel() {

    val newMovieLoader = MutableLiveData<LoadingState>()
    val topMovieLoader = MutableLiveData<LoadingState>()
    val topTvShowsLoader = MutableLiveData<LoadingState>()

    private val _movieDayData = MutableLiveData<TitleList.Data>()
    val movieDayData: LiveData<TitleList.Data> = _movieDayData

    private val _newMovieList = MutableLiveData<List<TitleList.Data>>()
    val newMovieList: LiveData<List<TitleList.Data>> = _newMovieList

    private val fetchNewMoviesList: MutableList<TitleList.Data> = ArrayList()

    val testMovies = MutableLiveData<TitleList.Data>()

    private val _topMovieList = MutableLiveData<List<TitleList.Data>>()
    val topMovieList: LiveData<List<TitleList.Data>> = _topMovieList

    private val fetchTopMoviesList: MutableList<TitleList.Data> = ArrayList()

    private val _topTvShowList = MutableLiveData<List<TitleList.Data>>()
    val topTvShowList: LiveData<List<TitleList.Data>> = _topTvShowList

    private val fetchTopTvShowsList: MutableList<TitleList.Data> = ArrayList()

    fun onSingleTitlePressed(start: Int, titleId: Int) {
        when (start) {
            AppConstants.NAV_TOP_MOVIES_TO_SINGLE -> navigateToNewFragment(
                    TopMoviesFragmentDirections.actionTopMoviesFragmentToSingleTitleFragmentNav(titleId)
            )
            AppConstants.NAV_TOP_TV_SHOWS_TO_SINGLE -> navigateToNewFragment(
                    TopTvShowsFragmentDirections.actionTopTvShowsFragmentToSingleTitleFragmentNav(titleId)
            )
            AppConstants.NAV_NEW_MOVIES_TO_SINGLE -> navigateToNewFragment(
                    NewMoviesFragmentDirections.actionNewMoviesFragmentToSingleTitleFragmentNav(titleId)
            )
        }
    }

    fun getNewMovies(page: Int) {
        viewModelScope.launch {
            newMovieLoader.value = LoadingState.LOADING
            when (val newMovies = repository.getNewMovies(page)) {
                is Result.Success -> {
                    val data = newMovies.data.data
                    fetchNewMoviesList.addAll(data)
                    _newMovieList.value = fetchNewMoviesList
                    newMovieLoader.value = LoadingState.LOADED
                }
                is Result.Error -> {
                    newToastMessage("ახალი ფილმები - ${newMovies.exception}")
                }
                is Result.Internet -> {
                    setNoInternet()
                }
            }
        }
    }

    fun getTopMovies(page: Int) {
        viewModelScope.launch {
            topMovieLoader.value = LoadingState.LOADING
            when (val topMovies = repository.getTopMovies(page)) {
                is Result.Success -> {
                    val data = topMovies.data.data
                    fetchTopMoviesList.addAll(data)
                    _topMovieList.value = fetchTopMoviesList
                    topMovieLoader.value = LoadingState.LOADED
                }
                is Result.Error -> {
                    newToastMessage("ტოპ ფილმები - ${topMovies.exception}")
                }
                is Result.Internet -> {
                    setNoInternet()
                }
            }
        }
    }

    fun getTopTvShows(page: Int) {
        viewModelScope.launch {
            topTvShowsLoader.value = LoadingState.LOADING
            when (val topTvShows = repository.getTopTvShows(page)) {
                is Result.Success -> {
                    val data = topTvShows.data.data
                    fetchTopTvShowsList.addAll(data)
                    _topTvShowList.value = fetchTopTvShowsList
                    topTvShowsLoader.value = LoadingState.LOADED
                }
                is Result.Error -> {
                    newToastMessage("ტოპ სერიალები- ${topTvShows.exception}")
                }
                is Result.Internet -> {
                    setNoInternet()
                }
            }
        }
    }
}