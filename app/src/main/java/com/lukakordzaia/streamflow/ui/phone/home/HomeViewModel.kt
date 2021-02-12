package com.lukakordzaia.streamflow.ui.phone.home

import android.content.Context
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lukakordzaia.streamflow.database.DbDetails
import com.lukakordzaia.streamflow.database.ImoviesDatabase
import com.lukakordzaia.streamflow.datamodels.DbTitleData
import com.lukakordzaia.streamflow.datamodels.TitleList
import com.lukakordzaia.streamflow.network.LoadingState
import com.lukakordzaia.streamflow.network.Result
import com.lukakordzaia.streamflow.repository.HomeRepository
import com.lukakordzaia.streamflow.ui.baseclasses.BaseViewModel
import com.lukakordzaia.streamflow.ui.phone.home.toplistfragments.TopMoviesFragmentDirections
import com.lukakordzaia.streamflow.ui.phone.home.toplistfragments.TopTvShowsFragmentDirections
import com.lukakordzaia.streamflow.utils.AppConstants
import kotlinx.coroutines.launch
import java.util.logging.Handler

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

    private val _topTvShowList = MutableLiveData<List<TitleList.Data>>()
    val topTvShowList: LiveData<List<TitleList.Data>> = _topTvShowList

    private val fetchTopTvShowsList: MutableList<TitleList.Data> = ArrayList()

    private val _dbList = MutableLiveData<List<DbTitleData>>()
    val dbList: LiveData<List<DbTitleData>> = _dbList

    private val dbTitles: MutableList<DbTitleData> = mutableListOf()

    fun onSingleTitlePressed(start: Int, titleId: Int) {
        when (start) {
            AppConstants.NAV_HOME_TO_SINGLE -> navigateToNewFragment(
                HomeFragmentDirections.actionHomeFragmentToSingleTitleFragmentNav(
                    titleId
                )
            )
            AppConstants.NAV_TOP_MOVIES_TO_SINGLE -> navigateToNewFragment(
                TopMoviesFragmentDirections.actionTopMoviesFragmentToSingleTitleFragmentNav(titleId)
            )
            AppConstants.NAV_TOP_TV_SHOWS_TO_SINGLE -> navigateToNewFragment(
                TopTvShowsFragmentDirections.actionTopTvShowsFragmentToSingleTitleFragmentNav(
                    titleId
                )
            )
        }
    }

    fun topMoviesMorePressed() {
        navigateToNewFragment(HomeFragmentDirections.actionHomeFragmentToTopMoviesFragment())
    }

    fun topTvShowsMorePressed() {
        navigateToNewFragment(HomeFragmentDirections.actionHomeFragmentToTopTvShowsFragment())
    }

    fun onDbTitlePressed(dbTitleData: DbTitleData) {
        navigateToNewFragment(
            HomeFragmentDirections.actionHomeFragmentToVideoPlayerFragmentNav(
                titleId = dbTitleData.id,
                chosenSeason = dbTitleData.season,
                chosenEpisode = dbTitleData.episode,
                isTvShow = dbTitleData.isTvShow,
                watchedTime = dbTitleData.watchedDuration,
                chosenLanguage = dbTitleData.language,
                trailerUrl = null
            )
        )
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
        dbTitles.clear()
    }

    fun getDbTitlesFromApi(dbDetails: List<DbDetails>) {
        dbTitles.clear()
        viewModelScope.launch {
            dbDetails.forEach {
                when (val dbTitles = repository.getSingleTitleData(it.titleId)) {
                    is Result.Success -> {
                        val data = dbTitles.data.data
                        this@HomeViewModel.dbTitles.add(
                            DbTitleData(
                                data.posters.data!!.x240,
                                data.duration,
                                data.id!!,
                                data.isTvShow!!,
                                data.primaryName,
                                data.originalName,
                                it.watchedDuration,
                                it.titleDuration,
                                it.season,
                                it.episode,
                                it.language
                            )
                        )
                    }
                    is Result.Error -> {
                        newToastMessage("ბაზის ფილმები - ${dbTitles.exception}")
                    }
                }
            }
            Log.d("savedtitles", "$dbTitles")
            _dbList.value = dbTitles
        }
    }

    fun getNewMovies(page: Int) {
        viewModelScope.launch {
            newMovieLoader.value = LoadingState.LOADING
            when (val newMovies = repository.getNewMovies(page)) {
                is Result.Success -> {
                    val data = newMovies.data.data
                    data.forEach {
                        fetchNewMoviesList.add(it)
                    }
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
                    data.forEach {
                        fetchTopMoviesList.add(it)
                    }
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
                    data.forEach {
                        fetchTopTvShowsList.add(it)
                    }
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

    fun refreshContent(page: Int) {
        getNewMovies(page)
        getTopMovies(page)
        getTopTvShows(page)
    }
}