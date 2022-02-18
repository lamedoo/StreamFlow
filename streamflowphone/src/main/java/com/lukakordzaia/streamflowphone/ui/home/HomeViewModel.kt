package com.lukakordzaia.streamflowphone.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lukakordzaia.core.utils.AppConstants
import com.lukakordzaia.core.baseclasses.BaseViewModel
import com.lukakordzaia.core.database.continuewatchingdb.ContinueWatchingRoom
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
    private val singleTitleUseCase: SingleTitleUseCase
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
            dbDetails.forEach { savedTitle ->
                when (val result = environment.singleTitleRepository.getSingleTitleData(savedTitle.titleId)) {
                    is ResultDomain.Success -> {
                        val data = result.data

                        val genres: MutableList<String> = ArrayList()
                        data.genres.data.forEach { it.primaryName?.let { name -> genres.add(name) } }

                        dbTitles.add(
                            ContinueWatchingModel(
                                poster = data.posters.data!!.x240,
                                cover = data.covers?.data?.x1050,
                                duration = data.duration,
                                id = savedTitle.titleId,
                                isTvShow = savedTitle.isTvShow,
                                primaryName = data.primaryName,
                                originalName = data.originalName,
                                imdbScore = data.rating.imdb?.let { it.score.toString() } ?: run { "N/A" },
                                releaseYear = data.year.toString(),
                                genres = genres,
                                seasonNum = if (data.seasons != null) {
                                    if (data.seasons!!.data.isNotEmpty()) data.seasons!!.data.size else 0
                                } else {
                                    0
                                },
                                watchedDuration = savedTitle.watchedDuration,
                                titleDuration = savedTitle.titleDuration,
                                season = savedTitle.season,
                                episode = savedTitle.episode,
                                language = savedTitle.language
                            )
                        )

                        _continueWatchingList.value = dbTitles
                    }
                    is ResultDomain.Error -> {
                        when (result.exception) {
                            AppConstants.NO_INTERNET_ERROR -> {}
                            else -> newToastMessage("ბაზის ფილმები - ${result.exception}")
                        }
                    }
                }
            }
            continueWatchingLoader.value = LoadingState.LOADED
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