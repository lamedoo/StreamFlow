package com.lukakordzaia.streamflow.ui.phone.home

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lukakordzaia.streamflow.database.DbDetails
import com.lukakordzaia.streamflow.database.ImoviesDatabase
import com.lukakordzaia.streamflow.datamodels.DbTitleData
import com.lukakordzaia.streamflow.datamodels.TitleList
import com.lukakordzaia.streamflow.datamodels.VideoPlayerData
import com.lukakordzaia.streamflow.network.LoadingState
import com.lukakordzaia.streamflow.network.Result
import com.lukakordzaia.streamflow.repository.HomeRepository
import com.lukakordzaia.streamflow.ui.baseclasses.BaseViewModel
import com.lukakordzaia.streamflow.ui.phone.home.toplistfragments.TopMoviesFragmentDirections
import com.lukakordzaia.streamflow.ui.phone.home.toplistfragments.TopTvShowsFragmentDirections
import com.lukakordzaia.streamflow.utils.AppConstants
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: HomeRepository) : BaseViewModel() {

    val movieDayLoader = MutableLiveData<LoadingState>()
    val newMovieLoader = MutableLiveData<LoadingState>()
    val topMovieLoader = MutableLiveData<LoadingState>()
    val topTvShowsLoader = MutableLiveData<LoadingState>()

    private val _movieDayData = MutableLiveData<TitleList.Data>()
    val movieDayData: LiveData<TitleList.Data> = _movieDayData

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
    private val continueWatchingTitlesFirestore: MutableList<DbDetails> = mutableListOf()

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

    fun onContinueWatchingPressed(dbTitleData: DbTitleData) {
        navigateToNewFragment(
                HomeFragmentDirections.actionHomeFragmentToVideoPlayerFragmentNav(
                        VideoPlayerData(
                                dbTitleData.id,
                                dbTitleData.isTvShow,
                                dbTitleData.season,
                                dbTitleData.language,
                                dbTitleData.episode,
                                dbTitleData.watchedDuration,
                                null
                        )
                )
        )
    }

    fun getContinueWatchingFromFirestore() {
        viewModelScope.launch {
            val data = repository.getContinueWatchingFromFirestore(currentUser()!!.uid)

            if (data != null) {
                for (title in data.documents) {
                    continueWatchingTitlesFirestore.add(
                            DbDetails(
                                    title.data!!["id"].toString().toInt(),
                                    title.data!!["language"].toString(),
                                    title.data!!["continueFrom"] as Long,
                                    title.data!!["titleDuration"] as Long,
                                    title.data!!["isTvShow"] as Boolean,
                                    title.data!!["season"].toString().toInt(),
                                    title.data!!["episode"].toString().toInt()
                            )
                    )
                }
                getContinueWatchingTitlesFromApi(continueWatchingTitlesFirestore)
            }
        }
    }

    fun getContinueWatchingFromRoom(context: Context): LiveData<List<DbDetails>> {
        return repository.getContinueWatchingFromRoom(roomDb(context)!!)
    }

    fun deleteSingleContinueWatchingFromRoom(context: Context, titleId: Int) {
        viewModelScope.launch {
            repository.deleteSingleContinueWatchingFromRoom(roomDb(context)!!, titleId)
        }
    }

    fun deleteSingleContinueWatchingFromFirestore(titleId: Int) {
        viewModelScope.launch {
            val deleteTitle = repository.deleteSingleContinueWatchingFromFirestore(currentUser()!!.uid, titleId)
            if (deleteTitle) {
                clearContinueWatchingTitleList()
                getContinueWatchingFromFirestore()
            } else {
                newToastMessage("საწმუხაროდ, ვერ მოხერხდა წაშლა")
            }
        }
    }

    fun clearContinueWatchingTitleList() {
        continueWatchingTitlesFirestore.clear()
        dbTitles.clear()
        _dbList.value = mutableListOf()
    }

    fun getContinueWatchingTitlesFromApi(dbDetails: List<DbDetails>) {
        viewModelScope.launch {
            dbDetails.forEach {
                when (val databaseTitles = repository.getSingleTitleData(it.titleId)) {
                    is Result.Success -> {
                        val data = databaseTitles.data.data
                        dbTitles.add(
                                DbTitleData(
                                        data.posters.data!!.x240,
                                        data.duration,
                                        data.id,
                                        data.isTvShow,
                                        data.primaryName,
                                        data.originalName,
                                        it.watchedDuration,
                                        it.titleDuration,
                                        it.season,
                                        it.episode,
                                        it.language
                                )
                        )
                        Log.d("savedtitles", "$databaseTitles")
                        _dbList.value = dbTitles
                    }
                    is Result.Error -> {
                        newToastMessage("ბაზის ფილმები - ${databaseTitles.exception}")
                    }
                }
            }
        }
    }

    fun getMovieDay() {
        viewModelScope.launch {
            movieDayLoader.value = LoadingState.LOADING
            when (val movieDay = repository.getMovieDay()) {
                is Result.Success -> {
                    val data = movieDay.data.data
                    _movieDayData.value = data[0]

                    movieDayLoader.value = LoadingState.LOADED
                }
                is Result.Error -> {
                    newToastMessage("დღის ფილმი - ${movieDay.exception}")
                }
                is Result.Internet -> {
                    setNoInternet()
                }
            }
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
                    when (topMovies.exception) {
                        
                    }
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
        getMovieDay()
        getNewMovies(page)
        getTopMovies(page)
        getTopTvShows(page)
    }
}