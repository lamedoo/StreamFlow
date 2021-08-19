package com.lukakordzaia.streamflow.ui.tv.tvsingletitle.tvtitledetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lukakordzaia.streamflow.database.continuewatchingdb.ContinueWatchingRoom
import com.lukakordzaia.streamflow.datamodels.AddFavoritesModel
import com.lukakordzaia.streamflow.datamodels.SingleTitleModel
import com.lukakordzaia.streamflow.network.FirebaseContinueWatchingCallBack
import com.lukakordzaia.streamflow.network.LoadingState
import com.lukakordzaia.streamflow.network.Result
import com.lukakordzaia.streamflow.ui.baseclasses.BaseViewModel
import com.lukakordzaia.streamflow.utils.AppConstants
import com.lukakordzaia.streamflow.utils.toSingleTitleModel
import kotlinx.coroutines.launch

class TvTitleDetailsViewModel : BaseViewModel() {
    val traktFavoriteLoader = MutableLiveData<LoadingState>()

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
        if (currentUser() == null) {
            getSingleContinueWatchingFromRoom(titleId)
        } else {
            checkContinueWatchingInFirestore(titleId)
        }
    }

    private fun getSingleContinueWatchingFromRoom(titleId: Int) {
        val data = environment.databaseRepository.getSingleContinueWatchingFromRoom(titleId)

        _continueWatchingDetails.addSource(data) {
            _continueWatchingDetails.value = it
        }
    }

    private fun checkContinueWatchingInFirestore(titleId: Int) {
        environment.databaseRepository.checkContinueWatchingInFirestore(currentUser()!!.uid, titleId, object : FirebaseContinueWatchingCallBack {
            override fun continueWatchingTitle(title: ContinueWatchingRoom?) {
                _continueWatchingDetails.value = title
            }

        })
    }

    fun deleteSingleContinueWatchingFromRoom(titleId: Int) {
        viewModelScope.launch {
            environment.databaseRepository.deleteSingleContinueWatchingFromRoom(titleId)
        }
    }

    fun deleteSingleContinueWatchingFromFirestore(titleId: Int) {
        viewModelScope.launch {
            val deleteTitle = environment.databaseRepository.deleteSingleContinueWatchingFromFirestore(currentUser()!!.uid, titleId)
            if (!deleteTitle) {
                newToastMessage("საწმუხაროდ, ვერ მოხერხდა წაშლა")
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

    fun addTitleToFavorites(info: SingleTitleModel) {
        if (currentUser() != null) {
            traktFavoriteLoader.value = LoadingState.LOADING
            viewModelScope.launch {
                val addToFavorites = environment.watchlistRepository.addTitleToFavorites(currentUser()!!.uid, AddFavoritesModel(
                        info.nameEng!!,
                        info.isTvShow,
                        info.id,
                        info.imdbId!!
                ))
                if (addToFavorites) {
                    newToastMessage("ფილმი დაემატა ფავორიტებში")
                    _addToFavorites.value = true
                    traktFavoriteLoader.value = LoadingState.LOADED
                } else {
                    newToastMessage("სამწუხაროდ ვერ მოხერხდა ფავორიტებში დამატება")
                    _addToFavorites.value = false
                    traktFavoriteLoader.value = LoadingState.LOADED
                }
            }
        } else {
            newToastMessage("ფავორიტებში დასამატებლად, გაიარეთ ავტორიზაცია")
        }
    }

    fun removeTitleFromFavorites(titleId: Int) {
        traktFavoriteLoader.value = LoadingState.LOADING
        viewModelScope.launch {
            val removeFromFavorites = environment.watchlistRepository.removeTitleFromFavorites(currentUser()!!.uid, titleId)
            if (removeFromFavorites) {
                _addToFavorites.value = false
                traktFavoriteLoader.value = LoadingState.LOADED
                newToastMessage("წარმატებით წაიშალა ფავორიტებიდან")
            } else {
                _addToFavorites.value = true
                traktFavoriteLoader.value = LoadingState.LOADED
            }
        }
    }

    fun checkTitleInFirestore(titleId: Int) {
        if (currentUser() != null) {
            traktFavoriteLoader.value = LoadingState.LOADING
            viewModelScope.launch {
                val checkTitle = environment.watchlistRepository.checkTitleInFavorites(currentUser()!!.uid, titleId)
                _addToFavorites.value = checkTitle!!.data != null
                traktFavoriteLoader.value = LoadingState.LOADED
            }
        } else {
            _addToFavorites.value = false
        }
    }

    fun setStartedWatching(started: Boolean) {
        _startedWatching.value = started
    }
}