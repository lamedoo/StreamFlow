package com.lukakordzaia.streamflowphone.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lukakordzaia.core.utils.AppConstants
import com.lukakordzaia.core.baseclasses.BaseViewModel
import com.lukakordzaia.core.domain.domainmodels.ContinueWatchingModel
import com.lukakordzaia.core.domain.domainmodels.NewSeriesModel
import com.lukakordzaia.core.domain.domainmodels.SingleTitleModel
import com.lukakordzaia.core.domain.usecases.*
import com.lukakordzaia.core.network.*
import kotlinx.coroutines.*

class HomeViewModel(
    private val movieDayUseCaseBase: MovieDayUseCaseBase,
    private val newMoviesUseCase: NewMoviesUseCase,
    private val topMoviesUseCase: TopMoviesUseCase,
    private val topTvShowsUseCase: TopTvShowsUseCase,
    private val newSeriesUseCase: NewSeriesUseCase,
    private val userSuggestionsUseCase: UserSuggestionsUseCase,
    private val continueWatchingUseCase: ContinueWatchingUseCase,
    private val dbDeleteSingleContinueWatchingUseCase: DbDeleteSingleContinueWatchingUseCase,
    private val hideContinueWatchingUseCase: HideContinueWatchingUseCase,
    private val dbAllContinueWatchingUseCase: DbAllContinueWatchingUseCase
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
                ContinueWatchingInfoBottomSheetDirections.actionContinueWatchingInfoFragmentToSingleTitleFragmentNav(titleId)
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
        continueWatchingLoader.postValue(LoadingState.LOADING)
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = dbAllContinueWatchingUseCase.invoke()) {
                is ResultDomain.Success -> {
                    _continueWatchingList.postValue(result.data)
                    continueWatchingLoader.postValue(LoadingState.LOADED)
                }
                is ResultDomain.Error -> {
                    when (result.exception) {
                        AppConstants.NO_INTERNET_ERROR -> {}
                        else -> newToastMessage("ბაზის ფილმები - ${result.exception}")
                    }
                }
            }
        }
    }

    private fun getContinueWatching() {
        continueWatchingLoader.value = LoadingState.LOADING
        viewModelScope.launch {
            when (val result = continueWatchingUseCase.invoke()) {
                is ResultDomain.Success -> {
                    _continueWatchingList.postValue(result.data)
                    continueWatchingLoader.value = LoadingState.LOADED
                }
                is ResultDomain.Error -> {
                    when (result.exception) {
                        AppConstants.NO_INTERNET_ERROR -> setNoInternet()
                        else -> newToastMessage("განაგრძეთ ყურება - ${result.exception}")
                    }
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
        viewModelScope.launch(Dispatchers.IO) {
            dbDeleteSingleContinueWatchingUseCase.invoke(titleId)
            hideContinueWatchingLoader.value = LoadingState.LOADED
        }
    }

    private fun hideSingleContinueWatching(titleId: Int) {
        hideContinueWatchingLoader.value = LoadingState.LOADING
        viewModelScope.launch {
            when (val result = hideContinueWatchingUseCase.invoke(titleId)) {
                is ResultDomain.Success -> {
                    newToastMessage("წაიშალა განაგრძეთ ყურების სიიდან")
                    checkAuthDatabase()
                    hideContinueWatchingLoader.value = LoadingState.LOADED
                }
                is ResultDomain.Error -> {
                    when (result.exception) {
                        AppConstants.NO_INTERNET_ERROR -> setNoInternet()
                        else -> newToastMessage("სამწუხაროდ ვერ მოხერხდა წაშლა")
                    }
                }
            }
        }
    }

    private fun clearContinueWatchingTitleList() {
        _continueWatchingList.value = mutableListOf()
    }

    private suspend fun getUserSuggestions() {
        if (sharedPreferences.getLoginToken() != "") {
            when (val result = userSuggestionsUseCase.invoke(sharedPreferences.getUserId())) {
                is ResultDomain.Success -> {
                    _userSuggestionsList.postValue(result.data)
                }
                is ResultDomain.Error -> {
                    when (result.exception) {
                        AppConstants.NO_INTERNET_ERROR -> {}
                        else -> newToastMessage("შემოთავაზებული ფილმები - ${result.exception}")
                    }
                }
            }
        }
    }

    private suspend fun getMovieDay() {
        when (val result = movieDayUseCaseBase.invoke()) {
            is ResultDomain.Success -> {
                _movieDayData.postValue(listOf(result.data[0]))
            }
            is ResultDomain.Error -> {
                when (result.exception) {
                    AppConstants.NO_INTERNET_ERROR -> setNoInternet()
                    else -> newToastMessage("დღის ფილმი - ${result.exception}")
                }
            }
        }
    }

    private suspend fun getNewMovies(page: Int) {
        when (val result = newMoviesUseCase.invoke(page)) {
            is ResultDomain.Success -> {
                _newMovieList.postValue(result.data)
            }
            is ResultDomain.Error -> {
                when (result.exception) {
                    AppConstants.NO_INTERNET_ERROR -> {}
                    else -> newToastMessage("ახალი ფილმები - ${result.exception}")
                }
            }
        }
    }

    private suspend fun getTopMovies(page: Int) {
        when (val result = topMoviesUseCase.invoke(page)) {
            is ResultDomain.Success -> {
                _topMovieList.postValue(result.data)
            }
            is ResultDomain.Error -> {
                when (result.exception) {
                    AppConstants.NO_INTERNET_ERROR -> {}
                    else -> newToastMessage("ტოპ ფილმები - ${result.exception}")
                }
            }
        }
    }

    private suspend fun getTopTvShows(page: Int) {
        when (val result = topTvShowsUseCase.invoke(page)) {
            is ResultDomain.Success -> {
                _topTvShowList.postValue(result.data)
            }
            is ResultDomain.Error -> {
                when (result.exception) {
                    AppConstants.NO_INTERNET_ERROR -> {}
                    else -> newToastMessage("ტოპ სერიალები - ${result.exception}")
                }
            }
        }
    }

    private suspend fun getNewSeries(page: Int) {
        when (val result = newSeriesUseCase.invoke(page)) {
            is ResultDomain.Success -> {
                _newSeriesList.postValue(result.data)
            }
            is ResultDomain.Error -> {
                when (result.exception) {
                    AppConstants.NO_INTERNET_ERROR -> {}
                    else -> newToastMessage("ახალი სერიალები - ${result.exception}")
                }
            }
        }
    }

    fun fetchContent(page: Int) {
        setGeneralLoader(LoadingState.LOADING)
        viewModelScope.launch(Dispatchers.IO) {
            getMovieDay()
            coroutineScope {
                val newMoviesDeferred = async { getNewMovies(page) }
                val topMoviesDeferred = async { getTopMovies(page) }
                val topTvShowsDeferred = async { getTopTvShows(page) }
                val newSeriesDeferred = async { getNewSeries(page) }
                val userSuggestionsDeferred = async { getUserSuggestions() }

                newMoviesDeferred.await()
                topMoviesDeferred.await()
                topTvShowsDeferred.await()
                newSeriesDeferred.await()
                userSuggestionsDeferred.await()
            }
            setGeneralLoader(LoadingState.LOADED)
        }
    }
}