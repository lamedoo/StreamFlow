package com.lukakordzaia.streamflowtv.ui.main

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lukakordzaia.core.BuildConfig
import com.lukakordzaia.core.baseclasses.BaseViewModel
import com.lukakordzaia.core.domain.domainmodels.ContinueWatchingModel
import com.lukakordzaia.core.domain.domainmodels.GithubReleaseModel
import com.lukakordzaia.core.domain.domainmodels.NewSeriesModel
import com.lukakordzaia.core.domain.domainmodels.SingleTitleModel
import com.lukakordzaia.core.domain.usecases.*
import com.lukakordzaia.core.network.LoadingState
import com.lukakordzaia.core.network.ResultDomain
import com.lukakordzaia.core.utils.AppConstants
import com.lukakordzaia.core.utils.DownloadHelper
import com.lukakordzaia.core.utils.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class TvMainViewModel(
    private val githubReleasesUseCase: GithubReleasesUseCase,
    private val newMoviesUseCase: NewMoviesUseCase,
    private val topMoviesUseCase: TopMoviesUseCase,
    private val topTvShowsUseCase: TopTvShowsUseCase,
    private val newSeriesUseCase: NewSeriesUseCase,
    private val userSuggestionsUseCase: UserSuggestionsUseCase,
    private val continueWatchingUseCase: ContinueWatchingUseCase,
    private val dbAllContinueWatchingUseCase: DbAllContinueWatchingUseCase
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

    private val _releaseUrl = MutableLiveData<Event<GithubReleaseModel>>()
    val releaseUrl: LiveData<Event<GithubReleaseModel>> = _releaseUrl

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

    fun checkGithubReleases(currentVersion: String) {
        if (!BuildConfig.DEBUG) {
            viewModelScope.launch(Dispatchers.IO) {
                when (val result = githubReleasesUseCase.invoke()) {
                    is ResultDomain.Success -> {
                        if (currentVersion != result.data.tag) {
                            result.data.downloadUrl?.let {
                                _releaseUrl.postValue(Event(result.data))
                            }
                        }
                    }
                    is ResultDomain.Error -> {

                    }
                }
            }
        }
    }

    fun downloadNewRelease(
        url: String,
        fileName: String,
        context: Context,
        description: String? = null,
        callback: ((String?) -> Unit)? = null
    ) {
        DownloadHelper.downloadFromUrl(
            context = context,
            url = url,
            fileName = fileName,
            title = null,
            description = description,
            usePublic = true,
            callback = callback
        )
    }

    private fun getContinueWatchingFromRoom() {
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = dbAllContinueWatchingUseCase.invoke()) {
                is ResultDomain.Success -> {
                    _continueWatchingList.postValue(result.data)
                }
                is ResultDomain.Error -> {
                    when (result.exception) {
                        AppConstants.NO_INTERNET_ERROR -> setNoInternet()
                        else -> newToastMessage("ბაზის ფილმები - ${result.exception}")
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