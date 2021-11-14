package com.lukakordzaia.streamflowphone.ui.phone.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lukakordzaia.core.AppConstants
import com.lukakordzaia.core.baseclasses.BaseViewModel
import com.lukakordzaia.core.database.continuewatchingdb.ContinueWatchingRoom
import com.lukakordzaia.core.datamodels.ContinueWatchingModel
import com.lukakordzaia.core.datamodels.NewSeriesModel
import com.lukakordzaia.core.datamodels.SingleTitleModel
import com.lukakordzaia.core.network.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel : BaseViewModel() {
    val continueWatchingLoader = MutableLiveData<LoadingState>()
    val hideContinueWatchingLoader = MutableLiveData<LoadingState>()

    private val _movieDayData = MutableLiveData<List<SingleTitleModel>>()
    val movieDayData: LiveData<List<SingleTitleModel>> = _movieDayData

    private val _newMovieList = MutableLiveData<List<SingleTitleModel>>()
    val newMovieList: LiveData<List<SingleTitleModel>> = _newMovieList

    private val _topMovieList = MutableLiveData<List<SingleTitleModel>>()
    val topMovieList: LiveData<List<SingleTitleModel>> = _topMovieList

    private val _topTvShowList = MutableLiveData<List<SingleTitleModel>>()
    val topTvShowList: LiveData<List<SingleTitleModel>> = _topTvShowList

    private val _newSeriesList = MutableLiveData<List<NewSeriesModel>>()
    val newSeriesList: LiveData<List<NewSeriesModel>> = _newSeriesList

    private val _userSuggestionsList = MutableLiveData<List<SingleTitleModel>>()
    val userSuggestionsList: LiveData<List<SingleTitleModel>> = _userSuggestionsList

    private val _continueWatchingList = MutableLiveData<List<ContinueWatchingModel>>()
    val continueWatchingList: LiveData<List<ContinueWatchingModel>> = _continueWatchingList

    private val _contWatchingData = MediatorLiveData<List<ContinueWatchingRoom>>()
    val contWatchingData: LiveData<List<ContinueWatchingRoom>> = _contWatchingData

    init {
        fetchContent(1)
    }

    fun onProfilePressed() {
        navigateToNewFragment(HomeFragmentDirections.actionHomeFragmentToProfileFragmentNav())
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

    fun onTopListPressed(type: Int) {
        navigateToNewFragment(HomeFragmentDirections.actionHomeFragmentToTopListFragment(type))
    }

    fun onContinueWatchingInfoPressed(titleId: Int, titleName: String) {
        navigateToNewFragment(HomeFragmentDirections.actionHomeFragmentToContinueWatchingInfoFragment(titleId, titleName))
    }

    fun checkAuthDatabase() {
        clearContinueWatchingTitleList()
        if (sharedPreferences.getLoginToken() != "") {
            getContinueWatching()
        } else {
            getContinueWatchingFromRoom()
        }
    }

    private fun getContinueWatchingFromRoom() {
        val data = environment.databaseRepository.getContinueWatchingFromRoom()

        _contWatchingData.addSource(data) {
            _contWatchingData.value = it
        }
    }

    fun getContinueWatchingTitlesFromApi(dbDetails: List<ContinueWatchingRoom>) {
        continueWatchingLoader.value = LoadingState.LOADING
        val dbTitles: MutableList<ContinueWatchingModel> = mutableListOf()
        viewModelScope.launch {
            dbDetails.forEach {
                when (val databaseTitles = environment.singleTitleRepository.getSingleTitleData(it.titleId)) {
                    is Result.Success -> {
                        val data = databaseTitles.data.data
                        dbTitles.add(
                            ContinueWatchingModel(
                                data.posters.data!!.x240,
                                data.duration,
                                it.titleId,
                                it.isTvShow,
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
            continueWatchingLoader.value = LoadingState.LOADED
        }
    }

    private fun getContinueWatching() {
        continueWatchingLoader.value = LoadingState.LOADING
        viewModelScope.launch {
            when (val watching = environment.homeRepository.getContinueWatching()) {
                is Result.Success -> {
                    val data = watching.data.data

                    _continueWatchingList.value = data.toContinueWatchingModel()
                    continueWatchingLoader.value = LoadingState.LOADED
                }
                is Result.Error -> {
                    newToastMessage("დღის ფილმი - ${watching.exception}")
                }
                is Result.Internet -> {
                    setNoInternet()
                }
            }
        }
    }

    fun deleteContinueWatching(titleId: Int) {
        if (sharedPreferences.getLoginToken() == "") {
            deleteSingleContinueWatchingFromRoom(titleId)
        } else {
            hideSingleContinueWatching(titleId)
        }
    }

    private fun deleteSingleContinueWatchingFromRoom(titleId: Int) {
        viewModelScope.launch {
            environment.databaseRepository.deleteSingleContinueWatchingFromRoom(titleId)
            hideContinueWatchingLoader.value = LoadingState.LOADED
        }
    }

    private fun hideSingleContinueWatching(titleId: Int) {
        hideContinueWatchingLoader.value = LoadingState.LOADING
        viewModelScope.launch {
            when (environment.homeRepository.hideTitleContinueWatching(titleId)) {
                is Result.Success -> {
                    newToastMessage("წაიშალა განაგრძეთ ყურების სიიდან")
                    checkAuthDatabase()
                    hideContinueWatchingLoader.value = LoadingState.LOADED
                }
                is Result.Error -> {
                    newToastMessage("საწმუხაროდ, ვერ მოხერხდა წაშლა")
                }
                is Result.Internet -> {
                    setNoInternet()
                }
            }
        }
    }

    private fun clearContinueWatchingTitleList() {
        _continueWatchingList.value = mutableListOf()
    }

    private fun getUserSuggestions() {
        if (sharedPreferences.getLoginToken() != "") {
            viewModelScope.launch {
                when (val suggestions = environment.homeRepository.getUserSuggestions(sharedPreferences.getUserId())) {
                    is Result.Success -> {
                        val data = suggestions.data.data
                        _userSuggestionsList.postValue(data.toTitleListModel())
                    }
                    is Result.Error -> {
                        newToastMessage("შემოთავაზებული ფილმები - ${suggestions.exception}")
                    }
                }
            }
        }
    }

    private suspend fun getMovieDay() {
        when (val movieDay = environment.homeRepository.getMovieDay()) {
            is Result.Success -> {
                val data = movieDay.data.data
                _movieDayData.postValue(listOf(data[0]).toTitleListModel())
            }
            is Result.Error -> {
                newToastMessage("დღის ფილმი - ${movieDay.exception}")
            }
            is Result.Internet -> {
                setNoInternet(true)
            }
        }
    }

    private suspend fun getNewMovies(page: Int) {
        when (val newMovies = environment.homeRepository.getNewMovies(page)) {
            is Result.Success -> {
                val data = newMovies.data.data
                _newMovieList.postValue(data.toTitleListModel())
            }
            is Result.Error -> {
                newToastMessage("ახალი ფილმები - ${newMovies.exception}")
            }
        }
    }

    private suspend fun getTopMovies(page: Int) {
        when (val topMovies = environment.homeRepository.getTopMovies(page)) {
            is Result.Success -> {
                val data = topMovies.data.data
                _topMovieList.postValue(data.toTitleListModel())
            }
            is Result.Error -> {
                newToastMessage("ტოპ ფილმები - ${topMovies.exception}")
            }
        }
    }

    private suspend fun getTopTvShows(page: Int) {
        when (val topTvShows = environment.homeRepository.getTopTvShows(page)) {
            is Result.Success -> {
                val data = topTvShows.data.data
                _topTvShowList.postValue(data.toTitleListModel())
            }
            is Result.Error -> {
                newToastMessage("ტოპ სერიალები- ${topTvShows.exception}")
            }
        }
    }

    private suspend fun getNewSeries(page: Int) {
        when (val newSeries = environment.homeRepository.getNewSeries(page)) {
            is Result.Success -> {
                val data = newSeries.data.data?.get(0)?.movies?.data
                _newSeriesList.postValue(data?.toNewSeriesModel())
            }
            is Result.Error -> {
                newToastMessage("ახალი სერიები- ${newSeries.exception}")
            }
        }
    }

    fun fetchContent(page: Int) {
        setNoInternet(false)
        setGeneralLoader(LoadingState.LOADING)
        getUserSuggestions()
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val getData = viewModelScope.launch {
                    getMovieDay()
                    getNewMovies(page)
                    getTopMovies(page)
                    getTopTvShows(page)
                    getNewSeries(page)
                }
                getData.join()
                setGeneralLoader(LoadingState.LOADED)
            }
        }
    }
}