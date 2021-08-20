package com.lukakordzaia.streamflow.ui.tv.tvsingletitle.tvtitledetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lukakordzaia.streamflow.database.continuewatchingdb.ContinueWatchingRoom
import com.lukakordzaia.streamflow.datamodels.SingleTitleModel
import com.lukakordzaia.streamflow.network.LoadingState
import com.lukakordzaia.streamflow.network.Result
import com.lukakordzaia.streamflow.ui.baseclasses.BaseViewModel
import com.lukakordzaia.streamflow.utils.AppConstants
import com.lukakordzaia.streamflow.utils.toSingleTitleModel
import kotlinx.coroutines.launch

class TvTitleDetailsViewModel : BaseViewModel() {
    val traktFavoriteLoader = MutableLiveData<LoadingState>()
    val hideContinueWatchingLoader = MutableLiveData<LoadingState>()
    val favoriteLoader = MutableLiveData<LoadingState>()

    private val _singleTitleData = MutableLiveData<SingleTitleModel>()
    val getSingleTitleResponse: LiveData<SingleTitleModel> = _singleTitleData

    private val _movieNotYetAdded = MutableLiveData<Boolean>()
    val movieNotYetAdded: LiveData<Boolean> = _movieNotYetAdded

    private val _dataLoader = MutableLiveData<LoadingState>()
    val dataLoader: LiveData<LoadingState> = _dataLoader

    private val _availableLanguages = MutableLiveData<MutableList<String>>()
    val availableLanguages: LiveData<MutableList<String>> = _availableLanguages

    private val _continueWatchingDetails = MediatorLiveData<ContinueWatchingRoom?>()
    val continueWatchingDetails: LiveData<ContinueWatchingRoom?> = _continueWatchingDetails

    private val _addToFavorites = MutableLiveData<Boolean>()
    val addToFavorites: LiveData<Boolean> = _addToFavorites

    private val _titleGenres = MutableLiveData<List<String>>()
    val titleGenres: LiveData<List<String>> = _titleGenres
    private val fetchTitleGenres: MutableList<String> = ArrayList()

    private val _startedWatching = MutableLiveData<Boolean>(false)
    val startedWatching: LiveData<Boolean> = _startedWatching

    fun getSingleTitleData(titleId: Int) {
        fetchTitleGenres.clear()
        viewModelScope.launch {
            _dataLoader.value = LoadingState.LOADING
            when (val info = environment.singleTitleRepository.getSingleTitleData(titleId)) {
                is Result.Success -> {
                    val data = info.data
                    _singleTitleData.value = data.toSingleTitleModel()

                    _dataLoader.value = LoadingState.LOADED

                    data.data.genres.data.forEach {
                        fetchTitleGenres.add(it.primaryName!!)
                    }
                    _titleGenres.value = fetchTitleGenres
                }
                is Result.Error -> {
                    newToastMessage(info.exception)
                }
                is Result.Internet -> {
                    setNoInternet()
                }
            }
        }
    }

    fun checkAuthDatabase(titleId: Int) {
        if (authSharedPreferences.getLoginToken() == "") {
            getSingleContinueWatchingFromRoom(titleId)
        }
    }

    private fun getSingleContinueWatchingFromRoom(titleId: Int) {
        val data = environment.databaseRepository.getSingleContinueWatchingFromRoom(titleId)

        _continueWatchingDetails.addSource(data) {
            _continueWatchingDetails.value = it
        }
    }

    fun deleteSingleContinueWatchingFromRoom(titleId: Int) {
        viewModelScope.launch {
            environment.databaseRepository.deleteSingleContinueWatchingFromRoom(titleId)
        }
    }

    fun hideSingleContinueWatching(titleId: Int) {
        hideContinueWatchingLoader.value = LoadingState.LOADING
        viewModelScope.launch {
            when (val hide = environment.homeRepository.hideTitleContinueWatching(titleId)) {
                is Result.Success -> {
                    newToastMessage("წაიშალა განაგრძეთ ყურების სიიდან")
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

    fun getSingleTitleFiles(movieId: Int) {
        viewModelScope.launch {
            when (val files = environment.singleTitleRepository.getSingleTitleFiles(movieId)) {
                is Result.Success -> {
                    val data = files.data.data
                    if (data.isNotEmpty()) {
                        val fetchLanguages: MutableList<String> = ArrayList()
                        data[0].files.forEach {
                            fetchLanguages.add(it.lang)
                        }
                        _availableLanguages.value = fetchLanguages

                        _movieNotYetAdded.value = false
                    } else {
                        _movieNotYetAdded.value = true
                    }
                }
                is Result.Error -> {
                    when (files.exception) {
                        AppConstants.UNKNOWN_ERROR -> {
                            _movieNotYetAdded.value = true
                        }
                    }
                }
                is Result.Internet -> {
                    setNoInternet()
                }
            }
        }
    }

    fun addWatchlistTitle(id: Int) {
        favoriteLoader.value = LoadingState.LOADING
        viewModelScope.launch {
            when (val delete = environment.watchlistRepository.addWatchlistTitle(id)) {
                is Result.Success -> {
                    newToastMessage("ფილმი დაემატა ფავორიტებში")
                    _addToFavorites.value = true
                    favoriteLoader.value = LoadingState.LOADED
                }
            }
        }
    }

    fun deleteWatchlistTitle(id: Int) {
        favoriteLoader.value = LoadingState.LOADING
        viewModelScope.launch {
            when (val delete = environment.watchlistRepository.deleteWatchlistTitle(id)) {
                is Result.Success -> {
                    _addToFavorites.value = false
                    favoriteLoader.value = LoadingState.LOADED
                    newToastMessage("წარმატებით წაიშალა ფავორიტებიდან")
                }
            }
        }
    }

    fun setStartedWatching(started: Boolean) {
        _startedWatching.value = started
    }
}