package com.lukakordzaia.streamflow.ui.phone.home.toplistfragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lukakordzaia.streamflow.network.models.imovies.response.titles.GetTitlesResponse
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

    private val _movieDayData = MutableLiveData<GetTitlesResponse.Data>()
    val movieDayData: LiveData<GetTitlesResponse.Data> = _movieDayData

    private val _newMovieList = MutableLiveData<List<GetTitlesResponse.Data>>()
    val newMovieList: LiveData<List<GetTitlesResponse.Data>> = _newMovieList

    private val fetchNewMoviesList: MutableList<GetTitlesResponse.Data> = ArrayList()

    val testMovies = MutableLiveData<GetTitlesResponse.Data>()

    private val _topMovieList = MutableLiveData<List<GetTitlesResponse.Data>>()
    val topMovieList: LiveData<List<GetTitlesResponse.Data>> = _topMovieList

    private val fetchTopMoviesList: MutableList<GetTitlesResponse.Data> = ArrayList()

    private val _topTvShowList = MutableLiveData<List<GetTitlesResponse.Data>>()
    val topTvShowList: LiveData<List<GetTitlesResponse.Data>> = _topTvShowList

    private val fetchTopTvShowsList: MutableList<GetTitlesResponse.Data> = ArrayList()

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