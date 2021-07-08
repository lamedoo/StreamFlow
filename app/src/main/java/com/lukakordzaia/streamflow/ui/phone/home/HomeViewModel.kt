package com.lukakordzaia.streamflow.ui.phone.home

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lukakordzaia.streamflow.database.continuewatchingdb.ContinueWatchingRoom
import com.lukakordzaia.streamflow.datamodels.DbTitleData
import com.lukakordzaia.streamflow.network.models.response.titles.GetTitlesResponse
import com.lukakordzaia.streamflow.network.FirebaseContinueWatchingListCallBack
import com.lukakordzaia.streamflow.network.LoadingState
import com.lukakordzaia.streamflow.network.Result
import com.lukakordzaia.streamflow.repository.HomeRepository
import com.lukakordzaia.streamflow.ui.baseclasses.BaseViewModel
import com.lukakordzaia.streamflow.utils.AppConstants
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: HomeRepository) : BaseViewModel() {

    val movieDayLoader = MutableLiveData<LoadingState>()
    val newMovieLoader = MutableLiveData<LoadingState>()
    val topMovieLoader = MutableLiveData<LoadingState>()
    val topTvShowsLoader = MutableLiveData<LoadingState>()

    private val _movieDayData = MutableLiveData<GetTitlesResponse.Data>()
    val movieDayData: LiveData<GetTitlesResponse.Data> = _movieDayData

    private val _newMovieList = MutableLiveData<List<GetTitlesResponse.Data>>()
    val newMovieList: LiveData<List<GetTitlesResponse.Data>> = _newMovieList

    private val _topMovieList = MutableLiveData<List<GetTitlesResponse.Data>>()
    val topMovieList: LiveData<List<GetTitlesResponse.Data>> = _topMovieList

    private val _topTvShowList = MutableLiveData<List<GetTitlesResponse.Data>>()
    val topTvShowList: LiveData<List<GetTitlesResponse.Data>> = _topTvShowList

    private val _continueWatchingList = MutableLiveData<List<DbTitleData>>()
    val continueWatchingList: LiveData<List<DbTitleData>> = _continueWatchingList

    init {
        getMovieDay()
        getNewMovies(1)
        getTopMovies(1)
        getTopTvShows(1)
    }

    fun onSingleTitlePressed(start: Int, titleId: Int) {
        when (start) {
            AppConstants.NAV_HOME_TO_SINGLE -> navigateToNewFragment(
                    HomeFragmentDirections.actionHomeFragmentToSingleTitleFragmentNav(titleId)
            )
            AppConstants.NAV_CONTINUE_WATCHING_TO_SINGLE -> navigateToNewFragment(
                    ContinueWatchingInfoFragmentDirections.actionContinueWatchingInfoFragmentToSingleTitleFragmentNav(titleId)
            )
        }
    }

    fun newMoviesMorePressed() {
        navigateToNewFragment(HomeFragmentDirections.actionHomeFragmentToNewMoviesFragment())
    }

    fun topMoviesMorePressed() {
        navigateToNewFragment(HomeFragmentDirections.actionHomeFragmentToTopMoviesFragment())
    }

    fun topTvShowsMorePressed() {
        navigateToNewFragment(HomeFragmentDirections.actionHomeFragmentToTopTvShowsFragment())
    }

    fun onContinueWatchingInfoPressed(titleId: Int, titleName: String) {
        navigateToNewFragment(HomeFragmentDirections.actionHomeFragmentToContinueWatchingInfoFragment(titleId, titleName))
    }

    fun getContinueWatchingFromFirestore() {
        repository.getContinueWatchingFromFirestore(currentUser()!!.uid, object : FirebaseContinueWatchingListCallBack {
            override fun continueWatchingList(titleList: MutableList<ContinueWatchingRoom>) {
                getContinueWatchingTitlesFromApi(titleList)
            }
        })
    }

    fun getContinueWatchingFromRoom(context: Context): LiveData<List<ContinueWatchingRoom>> {
        return repository.getContinueWatchingFromRoom(roomDb(context)!!)
    }

    fun getContinueWatchingTitlesFromApi(dbDetails: List<ContinueWatchingRoom>) {
        val dbTitles: MutableList<DbTitleData> = mutableListOf()
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

                        _continueWatchingList.value = dbTitles
                    }
                    is Result.Error -> {
                        newToastMessage("ბაზის ფილმები - ${databaseTitles.exception}")
                    }
                }
            }
        }
    }

    fun deleteContinueWatching(context: Context, titleId: Int) {
        if (currentUser() == null) {
            deleteSingleContinueWatchingFromRoom(context, titleId)
        } else {
            deleteSingleContinueWatchingFromFirestore(titleId)
        }
    }

    private fun deleteSingleContinueWatchingFromRoom(context: Context, titleId: Int) {
        viewModelScope.launch {
            repository.deleteSingleContinueWatchingFromRoom(roomDb(context)!!, titleId)
        }
    }

    private fun deleteSingleContinueWatchingFromFirestore(titleId: Int) {
        viewModelScope.launch {
            val deleteTitle = repository.deleteSingleContinueWatchingFromFirestore(currentUser()!!.uid, titleId)
            if (deleteTitle) {
                newToastMessage("წაიშალა განაგრძეთ ყურებიდან")
            } else {
                newToastMessage("საწმუხაროდ, ვერ მოხერხდა წაშლა")
            }
        }
    }

    fun clearContinueWatchingTitleList() {
        _continueWatchingList.value = mutableListOf()
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
                    _newMovieList.value = data
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
                    _topMovieList.value = data
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
                    _topTvShowList.value = data
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