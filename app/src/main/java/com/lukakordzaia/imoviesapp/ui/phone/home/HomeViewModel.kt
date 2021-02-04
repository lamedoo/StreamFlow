package com.lukakordzaia.imoviesapp.ui.phone.home

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lukakordzaia.imoviesapp.database.ImoviesDatabase
import com.lukakordzaia.imoviesapp.database.DbDetails
import com.lukakordzaia.imoviesapp.datamodels.TitleList
import com.lukakordzaia.imoviesapp.datamodels.WatchedTitleData
import com.lukakordzaia.imoviesapp.network.LoadingState
import com.lukakordzaia.imoviesapp.network.Result
import com.lukakordzaia.imoviesapp.repository.HomeRepository
import com.lukakordzaia.imoviesapp.ui.baseclasses.BaseViewModel
import com.lukakordzaia.imoviesapp.ui.phone.home.toplistfragments.TopMoviesFragmentDirections
import com.lukakordzaia.imoviesapp.ui.phone.home.toplistfragments.TopTvShowsFragmentDirections
import com.lukakordzaia.imoviesapp.utils.AppConstants
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: HomeRepository) : BaseViewModel() {

    val newMovieLoader = MutableLiveData<LoadingState>()
    val topMovieLoader = MutableLiveData<LoadingState>()
    val topTvShowsLoader = MutableLiveData<LoadingState>()

    private val _newMovieList = MutableLiveData<List<TitleList.Data>>()
    val newMovieList: LiveData<List<TitleList.Data>> = _newMovieList

    private val fetchNewMoviesList: MutableList<TitleList.Data> = ArrayList()

    private val _topMovieList = MutableLiveData<List<TitleList.Data>>()
    val topMovieList: LiveData<List<TitleList.Data>> = _topMovieList

    private val fetchTopMoviesList: MutableList<TitleList.Data> = ArrayList()

    private val _tvShowList = MutableLiveData<List<TitleList.Data>>()
    val tvShowList: LiveData<List<TitleList.Data>> = _tvShowList

    private val fetchTopTvShowsList: MutableList<TitleList.Data> = ArrayList()

    private val _watchedList = MutableLiveData<List<WatchedTitleData>>()
    val watchedList: LiveData<List<WatchedTitleData>> = _watchedList

    private val watchedTitles: MutableList<WatchedTitleData> = mutableListOf()

    fun onSingleTitlePressed(start: Int, titleId: Int) {
        when (start) {
            AppConstants.NAV_HOME_TO_SINGLE -> navigateToNewFragment(HomeFragmentDirections.actionHomeFragmentToSingleTitleFragmentNav(titleId))
            AppConstants.NAV_TOP_MOVIES_TO_SINGLE -> navigateToNewFragment(TopMoviesFragmentDirections.actionTopMoviesFragmentToSingleTitleFragmentNav(titleId))
            AppConstants.NAV_TOP_TV_SHOWS_TO_SINGLE -> navigateToNewFragment(TopTvShowsFragmentDirections.actionTopTvShowsFragmentToSingleTitleFragmentNav(titleId))
        }
    }

    fun topMoviesMorePressed() {
        navigateToNewFragment(HomeFragmentDirections.actionHomeFragmentToTopMoviesFragment())
    }

    fun topTvShowsMorePressed() {
        navigateToNewFragment(HomeFragmentDirections.actionHomeFragmentToTopTvShowsFragment())
    }

    fun onWatchedTitlePressed(watchedTitleData: WatchedTitleData) {
        navigateToNewFragment(HomeFragmentDirections.actionHomeFragmentToVideoPlayerFragmentNav(
            titleId = watchedTitleData.id,
            chosenSeason = watchedTitleData.season,
            chosenEpisode = watchedTitleData.episode,
            isTvShow = watchedTitleData.isTvShow,
            watchedTime = watchedTitleData.watchedTime,
            chosenLanguage = watchedTitleData.language,
            trailerUrl = null
        ))
    }

    fun getDbTitles(context: Context): LiveData<List<DbDetails>> {
        val database = ImoviesDatabase.getDatabase(context)?.getDao()
        return repository.getDbTitles(database!!)
    }

    fun deleteSingleTitleFromDb(context: Context, titleId: Int) {
        val database = ImoviesDatabase.getDatabase(context)?.getDao()
        viewModelScope.launch {
            repository.deleteSingleTitleFromDb(database!!, titleId)
        }
    }

    fun clearWatchedTitleList() {
        watchedTitles.clear()
    }

    fun getDbTitlesFromApi(dbDetails: List<DbDetails>) {
        watchedTitles.clear()
        viewModelScope.launch {
            dbDetails.forEach {
                when (val dbTitles = repository.getSingleTitleData(it.titleId)) {
                    is Result.Success -> {
                        val data = dbTitles.data.data
                        watchedTitles.add(WatchedTitleData(
                                data.posters.data!!.x240,
                                data.duration,
                                data.id!!,
                                data.isTvShow!!,
                                data.primaryName,
                                data.originalName,
                                it.watchedTime,
                                it.season,
                                it.episode,
                                it.language
                        ))
                    }
                    is Result.Error -> {
                        newToastMessage(dbTitles.exception)
                        Log.d("errordbtitles", dbTitles.exception)
                    }
                    is Result.Internet -> {
                        newToastMessage(dbTitles.exception)
                    }
                }
            }
            Log.d("savedtitles", "$watchedTitles")
            _watchedList.value = watchedTitles
        }
    }

    fun getNewMovies(page: Int) {
        viewModelScope.launch {
            newMovieLoader.value = LoadingState.LOADING
            when (val movies = repository.getNewMovies(page)) {
                is Result.Success -> {
                    val data = movies.data.data
                    data.forEach {
                        fetchNewMoviesList.add(it)
                    }
                    _newMovieList.value = fetchNewMoviesList
                    newMovieLoader.value = LoadingState.LOADED
                }
                is Result.Error -> {
                    newToastMessage(movies.exception)
                    Log.d("errornewmovies", movies.exception)
                }
                is Result.Internet -> {
                    newToastMessage("შეამოწმეთ ინტერნეტთან კავშირი")
                    getNewMovies(page)
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
                    data.forEach {
                        fetchTopMoviesList.add(it)
                    }
                    _topMovieList.value = fetchTopMoviesList
                    topMovieLoader.value = LoadingState.LOADED
                }
                is Result.Error -> {
                    newToastMessage(topMovies.exception)
                    Log.d("errornewmovies", topMovies.exception)
                }
                is Result.Internet -> {
                    getTopMovies(page)
                }
            }
        }
    }

    fun getTopTvShows(page: Int) {
        viewModelScope.launch {
            topTvShowsLoader.value = LoadingState.LOADING
            when (val tvShows = repository.getTopTvShows(page)) {
                is Result.Success -> {
                    val data = tvShows.data.data
                    data.forEach {
                        fetchTopTvShowsList.add(it)
                    }
                    _tvShowList.value = fetchTopTvShowsList
                    topTvShowsLoader.value = LoadingState.LOADED
                }
                is Result.Error -> {
                    newToastMessage(tvShows.exception)
                    Log.d("errornewtvshows", tvShows.exception)
                }
                is Result.Internet -> {
                    getTopTvShows(page)
                }
            }
        }
    }
}