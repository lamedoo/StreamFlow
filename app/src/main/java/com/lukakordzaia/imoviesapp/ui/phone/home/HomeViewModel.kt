package com.lukakordzaia.imoviesapp.ui.phone.home

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lukakordzaia.imoviesapp.database.ImoviesDatabase
import com.lukakordzaia.imoviesapp.database.WatchedDetails
import com.lukakordzaia.imoviesapp.datamodels.TitleList
import com.lukakordzaia.imoviesapp.datamodels.WatchedTitleData
import com.lukakordzaia.imoviesapp.network.Result
import com.lukakordzaia.imoviesapp.repository.HomeRepository
import com.lukakordzaia.imoviesapp.ui.baseclasses.BaseViewModel
import com.lukakordzaia.imoviesapp.ui.phone.home.toplistfragments.TopMoviesFragmentDirections
import com.lukakordzaia.imoviesapp.ui.phone.home.toplistfragments.TopTvShowsFragmentDirections
import com.lukakordzaia.imoviesapp.utils.AppConstants
import kotlinx.coroutines.launch

class HomeViewModel : BaseViewModel() {
    private val repository = HomeRepository()

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

    fun getWatchedFromDb(context: Context): LiveData<List<WatchedDetails>> {
        val database = ImoviesDatabase.getDatabase(context)?.getDao()
        return repository.getWatchedFromDb(database!!)
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

    fun getWatchedTitles(watchedDetails: List<WatchedDetails>) {
        watchedTitles.clear()
        viewModelScope.launch {
            watchedDetails.forEach {
                when (val watched = repository.getSingleTitleData(it.titleId)) {
                    is Result.Success -> {
                        val data = watched.data.data
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
                        setLoading(false)
                    }
                }
            }
            Log.d("savedtitles", "$watchedTitles")
            _watchedList.value = watchedTitles
        }
    }

    fun getNewMovies(page: Int) {
        viewModelScope.launch {
            when (val movies = repository.getNewMovies(page)) {
                is Result.Success -> {
                    val data = movies.data.data
                    data.forEach {
                        fetchNewMoviesList.add(it)
                    }
                    _newMovieList.value = fetchNewMoviesList
                    setLoading(false)
                }
                is Result.Error -> {
                    Log.d("errornewmovies", movies.exception)
                }
            }
        }
    }

    fun getTopMovies(page: Int) {
        viewModelScope.launch {
            when (val movies = repository.getTopMovies(page)) {
                is Result.Success -> {
                    val data = movies.data.data
                    data.forEach {
                        fetchTopMoviesList.add(it)
                    }
                    _topMovieList.value = fetchTopMoviesList
                    setLoading(false)
                }
                is Result.Error -> {
                    Log.d("errornewmovies", movies.exception)
                }
            }
        }
    }

    fun getTopTvShows(page: Int) {
        viewModelScope.launch {
            when (val tvShows = repository.getTopTvShows(page)) {
                is Result.Success -> {
                    val data = tvShows.data.data
                    data.forEach {
                        fetchTopTvShowsList.add(it)
                    }
                    _tvShowList.value = fetchTopTvShowsList
                    setLoading(false)
                }
                is Result.Error -> {
                    Log.d("errornewtvshows", tvShows.exception)
                }
            }
        }
    }
}