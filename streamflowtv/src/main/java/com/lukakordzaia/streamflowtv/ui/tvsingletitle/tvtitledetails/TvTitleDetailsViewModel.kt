package com.lukakordzaia.streamflowtv.ui.tvsingletitle.tvtitledetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lukakordzaia.core.utils.AppConstants
import com.lukakordzaia.core.baseclasses.BaseViewModel
import com.lukakordzaia.core.database.continuewatchingdb.ContinueWatchingRoom
import com.lukakordzaia.core.domain.domainmodels.SingleTitleModel
import com.lukakordzaia.core.domain.usecases.*
import com.lukakordzaia.core.network.LoadingState
import com.lukakordzaia.core.network.ResultData
import com.lukakordzaia.core.network.ResultDomain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TvTitleDetailsViewModel(
    private val singleTitleUseCase: SingleTitleUseCase,
    private val singleTitleFilesUseCase: SingleTitleFilesUseCase,
    private val addWatchlistUseCase: AddWatchlistUseCase,
    private val deleteWatchlistUseCase: DeleteWatchlistUseCase,
    private val dbSingleContinueWatchingUseCase: DbSingleContinueWatchingUseCase,
    private val dbDeleteSingleContinueWatchingUseCase: DbDeleteSingleContinueWatchingUseCase,
    private val hideContinueWatchingUseCase: HideContinueWatchingUseCase
) : BaseViewModel() {
    val hideContinueWatchingLoader = MutableLiveData<LoadingState>()
    val favoriteLoader = MutableLiveData<LoadingState>()

    private val _singleTitleData = MutableLiveData<SingleTitleModel>()
    val getSingleTitleResponse: LiveData<SingleTitleModel> = _singleTitleData

    private val _movieNotYetAdded = MutableLiveData<Boolean>()
    val movieNotYetAdded: LiveData<Boolean> = _movieNotYetAdded

    private val _availableLanguages = MutableLiveData<MutableList<String>>()
    val availableLanguages: LiveData<MutableList<String>> = _availableLanguages

    private val _continueWatchingDetails = MediatorLiveData<ContinueWatchingRoom?>()
    val continueWatchingDetails: LiveData<ContinueWatchingRoom?> = _continueWatchingDetails

    private val _addToFavorites = MutableLiveData<Boolean>()
    val addToFavorites: LiveData<Boolean> = _addToFavorites

    private val _titleGenres = MutableLiveData<List<String>>()
    val titleGenres: LiveData<List<String>> = _titleGenres
    private val fetchTitleGenres: MutableList<String> = ArrayList()

    fun getSingleTitleData(titleId: Int) {
        fetchTitleGenres.clear()
        viewModelScope.launch {
            setGeneralLoader(LoadingState.LOADING)
            when (val result = singleTitleUseCase.invoke(titleId)) {
                is ResultDomain.Success -> {
                    val data = result.data
                    _singleTitleData.value = data

                    setGeneralLoader(LoadingState.LOADED)

                    data.genres?.let { fetchTitleGenres.addAll(it) }
                    _titleGenres.postValue(fetchTitleGenres)

                    _addToFavorites.postValue(data.watchlist ?: false)
                }
                is ResultDomain.Error -> {
                    when (result.exception) {
                        AppConstants.NO_INTERNET_ERROR -> setNoInternet()
                        else -> newToastMessage("ინფორმაცია - ${result.exception}")
                    }
                }
            }
        }
    }

    private suspend fun getSingleContinueWatchingFromRoom(titleId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _continueWatchingDetails.addSource(dbSingleContinueWatchingUseCase.invoke(titleId)) {
                _continueWatchingDetails.value = it
            }
        }
    }

    fun getContinueWatching(titleId: Int) {
        viewModelScope.launch {
            when (val result = singleTitleUseCase.invoke(titleId)) {
                is ResultDomain.Success -> {
                    val data = result.data

                    if (sharedPreferences.getLoginToken() == "") {
                        getSingleContinueWatchingFromRoom(titleId)
                    } else {
                        if (data.currentSeason != null) {
                            _continueWatchingDetails.postValue(
                                ContinueWatchingRoom(
                                    titleId = titleId,
                                    language = data.currentLanguage!!,
                                    watchedDuration = data.watchedDuration!!,
                                    titleDuration = data.titleDuration!!,
                                    isTvShow = data.isTvShow,
                                    season = data.currentSeason!!,
                                    episode = data.currentEpisode!!
                                )
                            )
                        } else {
                            _continueWatchingDetails.postValue(null)
                        }
                    }
                }
                is ResultDomain.Error -> {
                    when (result.exception) {
                        AppConstants.NO_INTERNET_ERROR -> setNoInternet()
                        else -> newToastMessage("ინფორმაცია - ${result.exception}")
                    }
                }
            }
        }
    }

    fun deleteSingleContinueWatchingFromRoom(titleId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            dbDeleteSingleContinueWatchingUseCase.invoke(titleId)
            newToastMessage("წაიშალა ნახვების ისტორიიდან სიიდან")
        }
    }

    fun hideSingleContinueWatching(titleId: Int) {
        hideContinueWatchingLoader.value = LoadingState.LOADING
        viewModelScope.launch {
            when (val result = hideContinueWatchingUseCase.invoke(titleId)) {
                is ResultDomain.Success -> {
                    newToastMessage("დაიმალა განაგრძეთ ყურების სიიდან")
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

    fun getSingleTitleFiles(titleId: Int) {
        _movieNotYetAdded.value = false
        viewModelScope.launch {
            when (val result = singleTitleFilesUseCase.invoke(Pair(titleId, 1))) {
                is ResultDomain.Success -> {
                    if (result.data.isNotEmpty()) {
                        val fetchLanguages: MutableList<String> = ArrayList()
                        fetchLanguages.addAll(result.data[0].languages)
                        _availableLanguages.value = fetchLanguages

                        _movieNotYetAdded.value = false
                    } else {
                        _movieNotYetAdded.value = true
                    }
                }
                is ResultDomain.Error -> {
                    when (result.exception) {
                        AppConstants.NO_INTERNET_ERROR -> {}
                        AppConstants.UNKNOWN_ERROR -> _movieNotYetAdded.value = true
                        else -> newToastMessage("ენები - ${result.exception}")
                    }
                }
            }
        }
    }

    fun addWatchlistTitle(id: Int) {
        favoriteLoader.value = LoadingState.LOADING
        viewModelScope.launch {
            when (val result = addWatchlistUseCase.invoke(id)) {
                is ResultDomain.Success -> {
                    newToastMessage("ფილმი დაემატა ფავორიტებში")
                    _addToFavorites.value = true
                    favoriteLoader.value = LoadingState.LOADED
                }
                is ResultDomain.Error -> {
                    when (result.exception) {
                        AppConstants.NO_INTERNET_ERROR -> setNoInternet()
                        else -> newToastMessage("ვერ მოხერხდა დამატება - ${result.exception}")
                    }
                }
            }
        }
    }


    fun deleteWatchlistTitle(id: Int, fromWatchlist: Int?) {
        favoriteLoader.value = LoadingState.LOADING
        viewModelScope.launch {
            when (val result = deleteWatchlistUseCase.invoke(id)) {
                is ResultDomain.Success -> {
                    _addToFavorites.value = false
                    favoriteLoader.value = LoadingState.LOADED
                    newToastMessage("წარმატებით წაიშალა ფავორიტებიდან")

                    if (fromWatchlist != null) {
                        sharedPreferences.saveFromWatchlist(fromWatchlist)
                    }
                }
                is ResultDomain.Error -> {
                    when (result.exception) {
                        AppConstants.NO_INTERNET_ERROR -> setNoInternet()
                        else -> newToastMessage("ვერ მოხერხდა წაშლა - ${result.exception}")
                    }
                }
            }
        }
    }
}