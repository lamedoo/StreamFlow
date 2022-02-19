package com.lukakordzaia.streamflowphone.ui.phonesingletitle

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lukakordzaia.core.baseclasses.BaseViewModel
import com.lukakordzaia.core.database.continuewatchingdb.ContinueWatchingRoom
import com.lukakordzaia.core.domain.domainmodels.SingleTitleModel
import com.lukakordzaia.core.domain.usecases.*
import com.lukakordzaia.core.network.*
import com.lukakordzaia.core.network.models.imovies.response.singletitle.GetSingleTitleCastResponse
import com.lukakordzaia.core.utils.AppConstants
import kotlinx.coroutines.*

class PhoneSingleTitleViewModel(
    private val singleTitleUseCase: SingleTitleUseCase,
    private val singleTitleCastUseCase: SingleTitleCastUseCase,
    private val singleTitleRelatedUseCase: SingleTitleRelatedUseCase,
    private val addWatchlistUseCase: AddWatchlistUseCase,
    private val deleteWatchlistUseCase: DeleteWatchlistUseCase,
    private val dbSingleContinueWatchingUseCase: DbSingleContinueWatchingUseCase,
    private val singleTitleFilesUseCase: SingleTitleFilesUseCase
) : BaseViewModel() {
    val favoriteLoader = MutableLiveData<LoadingState>()

    private val _singleTitleData = MutableLiveData<SingleTitleModel>()
    val getSingleTitleResponse: LiveData<SingleTitleModel> = _singleTitleData

    private val _castData = MutableLiveData<List<GetSingleTitleCastResponse.Data>>()
    val castResponseDataGetSingle: LiveData<List<GetSingleTitleCastResponse.Data>> = _castData

    private val _titleGenres = MutableLiveData<List<String>>()
    val titleGenres: LiveData<List<String>> = _titleGenres

    private val _titleDirector = MutableLiveData<GetSingleTitleCastResponse.Data>()
    val titleDirector: LiveData<GetSingleTitleCastResponse.Data> = _titleDirector

    private val _singleTitleRelated = MutableLiveData<List<SingleTitleModel>>()
    val singleTitleRelated: LiveData<List<SingleTitleModel>> = _singleTitleRelated

    private val _addToFavorites = MutableLiveData<Boolean>()
    val addToFavorites: LiveData<Boolean> = _addToFavorites

    private val _continueWatchingDetails = MediatorLiveData<ContinueWatchingRoom?>()
    val continueWatchingDetails: LiveData<ContinueWatchingRoom?> = _continueWatchingDetails

    private val _movieNotYetAdded = MutableLiveData<Boolean>()
    val movieNotYetAdded: LiveData<Boolean> = _movieNotYetAdded

    private val _availableLanguages = MutableLiveData<MutableList<String>>()
    val availableLanguages: LiveData<MutableList<String>> = _availableLanguages

    fun onEpisodesPressed(titleId: Int, titleName: String, seasonNum: Int) {
        navigateToNewFragment(
            PhoneSingleTitleFragmentDirections.actionSingleTitleFragmentToChooseTitleDetailsFragment(
                titleId,
                titleName,
                seasonNum
            )
        )
    }

    fun onRelatedTitlePressed(titleId: Int) {
        navigateToNewFragment(PhoneSingleTitleFragmentDirections.actionSingleTitleFragmentSelf(titleId))
    }

    private suspend fun getSingleTitleData(titleId: Int) {
        val fetchTitleGenres: MutableList<String> = ArrayList()
        when (val result = singleTitleUseCase.invoke(titleId)) {
            is ResultDomain.Success -> {
                val data = result.data
                _singleTitleData.postValue(data)

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

                _addToFavorites.postValue(data.watchlist ?: false)

                data.genres?.let { fetchTitleGenres.addAll(it) }
                _titleGenres.postValue(fetchTitleGenres)
            }
            is ResultDomain.Error -> {
                when (result.exception) {
                    AppConstants.NO_INTERNET_ERROR -> setNoInternet()
                    else -> newToastMessage("ინფორმაცია - ${result.exception}")
                }
            }
        }
    }

    private suspend fun getTitleCast(titleId: Int) {
        when (val result = singleTitleCastUseCase.invoke(Pair(titleId, "cast"))) {
            is ResultDomain.Success -> {
                val data = result.data.data
                _castData.postValue(data)
            }
            is ResultDomain.Error -> {
                when (result.exception) {
                    AppConstants.NO_INTERNET_ERROR -> {}
                    else -> newToastMessage("მსახიობები - ${result.exception}")
                }
            }
        }
    }

    private suspend fun getTitleDirector(titleId: Int) {
        when (val result = singleTitleCastUseCase.invoke(Pair(titleId, "director"))) {
            is ResultDomain.Success -> {
                val data = result.data.data

                if (!data.isNullOrEmpty()) {
                    _titleDirector.postValue(data[0])
                }
            }
            is ResultDomain.Error -> {
                when (result.exception) {
                    AppConstants.NO_INTERNET_ERROR -> {}
                    else -> newToastMessage("რეჟისორი - ${result.exception}")
                }
            }
        }
    }

    private suspend fun getRelatedTitles(titleId: Int) {
        when (val result = singleTitleRelatedUseCase.invoke(titleId)) {
            is ResultDomain.Success -> {
                _singleTitleRelated.postValue(result.data)
            }
            is ResultDomain.Error -> {
                when (result.exception) {
                    AppConstants.NO_INTERNET_ERROR -> {}
                    else -> newToastMessage("მსგავსი - ${result.exception}")
                }
            }
        }
    }

    private suspend fun getSingleContinueWatchingFromRoom(titleId: Int) {
        _continueWatchingDetails.addSource(dbSingleContinueWatchingUseCase.invoke(titleId)) {
            _continueWatchingDetails.value = it
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

    fun deleteWatchlistTitle(id: Int) {
        favoriteLoader.value = LoadingState.LOADING
        viewModelScope.launch {
            when (val result = deleteWatchlistUseCase.invoke(id)) {
                is ResultDomain.Success -> {
                    _addToFavorites.value = false
                    favoriteLoader.value = LoadingState.LOADED
                    newToastMessage("წარმატებით წაიშალა ფავორიტებიდან")
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

    private suspend fun getAvailableLanguages(titleId: Int) {
        _movieNotYetAdded.postValue(false)
        viewModelScope.launch {
            when (val result = singleTitleFilesUseCase.invoke(Pair(titleId, 1))) {
                is ResultDomain.Success -> {
                    if (result.data.isNotEmpty()) {
                        val fetchLanguages: MutableList<String> = ArrayList()
                        fetchLanguages.addAll(result.data[0].languages)
                        _availableLanguages.postValue(fetchLanguages)

                        _movieNotYetAdded.postValue(false)
                    } else {
                        _movieNotYetAdded.postValue(true)
                    }
                }
                is ResultDomain.Error -> {
                    when (result.exception) {
                        AppConstants.NO_INTERNET_ERROR -> {}
                        AppConstants.UNKNOWN_ERROR -> _movieNotYetAdded.postValue(true)
                        else -> newToastMessage("ენები - ${result.exception}")
                    }
                }
            }
        }
    }

    fun fetchContent(titleId: Int) {
        setGeneralLoader(LoadingState.LOADING)
        viewModelScope.launch(Dispatchers.IO) {
            coroutineScope {
                val dataDeferred = async { getSingleTitleData(titleId) }
                val castDeferred = async { getTitleCast(titleId) }
                val directorDeferred = async { getTitleDirector(titleId) }
                val relatedDeferred = async { getRelatedTitles(titleId) }
                val languagesDeferred = async { getAvailableLanguages(titleId) }

                dataDeferred.await()
                castDeferred.await()
                directorDeferred.await()
                relatedDeferred.await()
                languagesDeferred.await()
            }
            setGeneralLoader(LoadingState.LOADED)
        }
    }
}











