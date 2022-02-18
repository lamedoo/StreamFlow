package com.lukakordzaia.streamflowtv.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lukakordzaia.core.baseclasses.BaseViewModel
import com.lukakordzaia.core.database.continuewatchingdb.ContinueWatchingRoom
import com.lukakordzaia.core.domain.domainmodels.ContinueWatchingModel
import com.lukakordzaia.core.domain.domainmodels.NewSeriesModel
import com.lukakordzaia.core.domain.domainmodels.SingleTitleModel
import com.lukakordzaia.core.domain.usecases.*
import com.lukakordzaia.core.network.*
import com.lukakordzaia.core.utils.AppConstants
import kotlinx.coroutines.*

class TvMainViewModel(
    private val newMoviesUseCase: NewMoviesUseCase,
    private val topMoviesUseCase: TopMoviesUseCase,
    private val topTvShowsUseCase: TopTvShowsUseCase,
    private val newSeriesUseCase: NewSeriesUseCase,
    private val userSuggestionsUseCase: UserSuggestionsUseCase,
    private val continueWatchingUseCase: ContinueWatchingUseCase,
    private val singleTitleUseCase: SingleTitleUseCase
) : BaseViewModel() {
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
        val dbTitles: MutableList<ContinueWatchingModel> = mutableListOf()
        viewModelScope.launch {
            dbDetails.forEach { savedTitle ->
                when (val result = singleTitleUseCase.invoke(savedTitle.titleId)) {
                    is ResultDomain.Success -> {
                        val data = result.data

                        dbTitles.add(
                            ContinueWatchingModel(
                                poster = data.poster,
                                cover = data.cover,
                                duration = data.duration?.toInt(),
                                id = savedTitle.titleId,
                                isTvShow = savedTitle.isTvShow,
                                primaryName = data.displayName,
                                originalName = data.nameEng,
                                imdbScore = data.imdbScore,
                                releaseYear = data.releaseYear,
                                genres = data.genres,
                                seasonNum = data.seasonNum,
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
        }
    }

    private fun getContinueWatching() {
        viewModelScope.launch {
            when (val result = continueWatchingUseCase.invoke()) {
                is ResultDomain.Success -> {
                    _continueWatchingList.postValue(result.data)
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