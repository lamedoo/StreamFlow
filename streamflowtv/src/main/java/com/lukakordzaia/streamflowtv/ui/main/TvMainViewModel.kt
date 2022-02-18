package com.lukakordzaia.streamflowtv.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lukakordzaia.core.baseclasses.BaseViewModel
import com.lukakordzaia.core.database.continuewatchingdb.ContinueWatchingRoom
import com.lukakordzaia.core.datamodels.ContinueWatchingModel
import com.lukakordzaia.core.datamodels.NewSeriesModel
import com.lukakordzaia.core.datamodels.SingleTitleModel
import com.lukakordzaia.core.network.*
import kotlinx.coroutines.*

class TvMainViewModel : BaseViewModel() {
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
            dbDetails.forEach {
                when (val databaseTitles = environment.singleTitleRepository.getSingleTitleData(it.titleId)) {
                    is Result.Success -> {
                        val data = databaseTitles.data.data
                        dbTitles.add(
                            ContinueWatchingModel(
                                data.posters.data!!.x240,
                                data.covers?.data?.x1050,
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
        }
    }

    private fun getContinueWatching() {
        viewModelScope.launch {
            when (val watching = environment.homeRepository.getContinueWatching()) {
                is Result.Success -> {
                    val data = watching.data.data
                    _continueWatchingList.value = data.toContinueWatchingModel()
                }
                is Result.Error -> {
                    newToastMessage("განაგრძე ყურება - ${watching.exception}")
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

    private suspend fun getNewMovies(page: Int) {
        when (val newMovies = environment.homeRepository.getNewMovies(page)) {
            is Result.Success -> {
                val data = newMovies.data.data
                _newMovieList.postValue(data.toTitleListModel())
            }
            is Result.Error -> {
                newToastMessage("ახალი ფილმები - ${newMovies.exception}")
            }
            is Result.Internet -> {
                setNoInternet()
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
        setGeneralLoader(LoadingState.LOADING)
        getUserSuggestions()
        viewModelScope.launch(Dispatchers.IO) {
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