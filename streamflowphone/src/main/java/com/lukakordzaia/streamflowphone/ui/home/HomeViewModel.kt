package com.lukakordzaia.streamflowphone.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lukakordzaia.core.utils.AppConstants
import com.lukakordzaia.core.baseclasses.BaseViewModel
import com.lukakordzaia.core.database.continuewatchingdb.ContinueWatchingRoom
import com.lukakordzaia.core.datamodels.ContinueWatchingModel
import com.lukakordzaia.core.datamodels.NewSeriesModel
import com.lukakordzaia.core.datamodels.SingleTitleModel
import com.lukakordzaia.core.domain.usecases.MovieDayUseCaseBase
import com.lukakordzaia.core.domain.usecases.NewMoviesUseCase
import com.lukakordzaia.core.network.*
import kotlinx.coroutines.*

class HomeViewModel(
    private val movieDayUseCaseBase: MovieDayUseCaseBase,
    private val newMoviesUseCase: NewMoviesUseCase
) : BaseViewModel() {
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
                    is ResultData.Success -> {
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
                    is ResultData.Error -> {
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
                is ResultData.Success -> {
                    val data = watching.data.data

                    _continueWatchingList.value = data.toContinueWatchingModel()
                    continueWatchingLoader.value = LoadingState.LOADED
                }
                is ResultData.Error -> {
                    newToastMessage("განაგრძეთ ყურება - ${watching.exception}")
                }
                is ResultData.Internet -> {
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
                is ResultData.Success -> {
                    newToastMessage("წაიშალა განაგრძეთ ყურების სიიდან")
                    checkAuthDatabase()
                    hideContinueWatchingLoader.value = LoadingState.LOADED
                }
                is ResultData.Error -> {
                    newToastMessage("საწმუხაროდ, ვერ მოხერხდა წაშლა")
                }
                is ResultData.Internet -> {
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
                    is ResultData.Success -> {
                        val data = suggestions.data.data
                        _userSuggestionsList.postValue(data.toTitleListModel())
                    }
                    is ResultData.Error -> {
                        newToastMessage("შემოთავაზებული ფილმები - ${suggestions.exception}")
                    }
                }
            }
        }
    }

    private suspend fun getMovieDay() {
        when (val movieDay = movieDayUseCaseBase.invoke()) {
            is ResultDomain.Success -> {
                val data = movieDay.data[0]
                _movieDayData.postValue(listOf(data))
            }
            is ResultDomain.Error -> {
                newToastMessage("დღის ფილმი - ${movieDay.exception}")
            }
            is ResultDomain.Internet -> {
                setNoInternet()
            }
        }
    }

    private suspend fun getNewMovies(page: Int) {
        when (val newMovies = newMoviesUseCase.invoke(page)) {
            is ResultDomain.Success -> {
                val data = newMovies.data
                _newMovieList.postValue(data)
            }
            is ResultDomain.Error -> {
                newToastMessage("ახალი ფილმები - ${newMovies.exception}")
            }
        }
    }

    private suspend fun getTopMovies(page: Int) {
        when (val topMovies = environment.homeRepository.getTopMovies(page)) {
            is ResultData.Success -> {
                val data = topMovies.data.data
                _topMovieList.postValue(data.toTitleListModel())
            }
            is ResultData.Error -> {
                newToastMessage("ტოპ ფილმები - ${topMovies.exception}")
            }
        }
    }

    private suspend fun getTopTvShows(page: Int) {
        when (val topTvShows = environment.homeRepository.getTopTvShows(page)) {
            is ResultData.Success -> {
                val data = topTvShows.data.data
                _topTvShowList.postValue(data.toTitleListModel())
            }
            is ResultData.Error -> {
                newToastMessage("ტოპ სერიალები- ${topTvShows.exception}")
            }
        }
    }

    private suspend fun getNewSeries(page: Int) {
        when (val newSeries = environment.homeRepository.getNewSeries(page)) {
            is ResultData.Success -> {
                val data = newSeries.data.data?.get(0)?.movies?.data
                _newSeriesList.postValue(data?.toNewSeriesModel())
            }
            is ResultData.Error -> {
                newToastMessage("ახალი სერიები- ${newSeries.exception}")
            }
        }
    }

    fun fetchContent(page: Int) {
        setGeneralLoader(LoadingState.LOADING)
        getUserSuggestions()
        viewModelScope.launch(Dispatchers.IO) {
            getMovieDay()
            coroutineScope {
                val newMoviesDeferred = async { getNewMovies(page) }
                val topMoviesDeferred = async { getTopMovies(page) }
                val topTvShowsDeferred = async { getTopTvShows(page) }
                val newSeriesDeferred = async { getNewSeries(page) }

                newMoviesDeferred.await()
                topMoviesDeferred.await()
                topTvShowsDeferred.await()
                newSeriesDeferred.await()
            }
            setGeneralLoader(LoadingState.LOADED)
        }
    }
}