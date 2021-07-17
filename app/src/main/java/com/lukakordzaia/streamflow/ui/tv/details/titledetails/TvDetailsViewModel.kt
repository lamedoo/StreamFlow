package com.lukakordzaia.streamflow.ui.tv.details.titledetails

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lukakordzaia.streamflow.database.continuewatchingdb.ContinueWatchingRoom
import com.lukakordzaia.streamflow.database.ImoviesDatabase
import com.lukakordzaia.streamflow.datamodels.AddTitleToFirestore
import com.lukakordzaia.streamflow.datamodels.SingleTitleModel
import com.lukakordzaia.streamflow.helpers.MapTitleData
import com.lukakordzaia.streamflow.network.models.imovies.response.singletitle.GetSingleTitleResponse
import com.lukakordzaia.streamflow.network.FirebaseContinueWatchingCallBack
import com.lukakordzaia.streamflow.network.LoadingState
import com.lukakordzaia.streamflow.network.Result
import com.lukakordzaia.streamflow.repository.TvDetailsRepository
import com.lukakordzaia.streamflow.ui.baseclasses.BaseViewModel
import com.lukakordzaia.streamflow.utils.AppConstants
import kotlinx.coroutines.launch

class TvDetailsViewModel(private val repository: TvDetailsRepository) : BaseViewModel() {
    val traktFavoriteLoader = MutableLiveData<LoadingState>()

    private val _singleTitleData = MutableLiveData<SingleTitleModel>()
    val getSingleTitleResponse: LiveData<SingleTitleModel> = _singleTitleData

    private val _movieNotYetAdded = MutableLiveData<Boolean>()
    val movieNotYetAdded: LiveData<Boolean> = _movieNotYetAdded

    private val _dataLoader = MutableLiveData<LoadingState>()
    val dataLoader: LiveData<LoadingState> = _dataLoader

    private val _availableLanguages = MutableLiveData<MutableList<String>>()
    val availableLanguages: LiveData<MutableList<String>> = _availableLanguages

    private val _continueWatchingDetails = MutableLiveData<ContinueWatchingRoom>(null)
    val continueWatchingDetails: LiveData<ContinueWatchingRoom> = _continueWatchingDetails

    private val _addToFavorites = MutableLiveData<Boolean>()
    val addToFavorites: LiveData<Boolean> = _addToFavorites

    private val _titleGenres = MutableLiveData<List<String>>()
    val titleGenres: LiveData<List<String>> = _titleGenres
    private val fetchTitleGenres: MutableList<String> = ArrayList()

    fun getSingleTitleData(titleId: Int) {
        fetchTitleGenres.clear()
        viewModelScope.launch {
            _dataLoader.value = LoadingState.LOADING
            when (val info = repository.getSingleTitleData(titleId)) {
                is Result.Success -> {
                    val data = info.data.data
                    _singleTitleData.value = MapTitleData().single(data)

                    _dataLoader.value = LoadingState.LOADED

                    data.genres.data.forEach {
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

    fun checkContinueWatchingTitleInRoom(context: Context, titleId: Int): LiveData<Boolean> {
        val database = ImoviesDatabase.getDatabase(context)?.continueWatchingDao()
        return repository.checkContinueWatchingTitleInRoom(database!!, titleId)
    }

    fun getSingleContinueWatchingFromRoom(context: Context, titleId: Int){
        viewModelScope.launch {
            _continueWatchingDetails.value = repository.getSingleContinueWatchingFromRoom(roomDb(context)!!, titleId)
        }
    }

    fun deleteSingleContinueWatchingFromRoom(context: Context, titleId: Int) {
        val database = ImoviesDatabase.getDatabase(context)?.continueWatchingDao()
        viewModelScope.launch {
            repository.deleteSingleContinueWatchingFromRoom(database!!, titleId)
        }
    }

    fun deleteSingleContinueWatchingFromFirestore(titleId: Int) {
        viewModelScope.launch {
            val deleteTitle = repository.deleteSingleContinueWatchingFromFirestore(currentUser()!!.uid, titleId)
            if (!deleteTitle) {
                newToastMessage("საწმუხაროდ, ვერ მოხერხდა წაშლა")
            }
        }
    }

    fun checkContinueWatchingInFirestore(titleId: Int) {
        repository.checkContinueWatchingInFirestore(currentUser()!!.uid, titleId, object : FirebaseContinueWatchingCallBack {
            override fun continueWatchingTitle(title: ContinueWatchingRoom) {
                _continueWatchingDetails.value = title
            }

        })
    }

    fun getSingleTitleFiles(movieId: Int) {
        viewModelScope.launch {
            when (val files = repository.getSingleTitleFiles(movieId)) {
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

    fun addTitleToFirestore(info: SingleTitleModel) {
        if (currentUser() != null) {
            traktFavoriteLoader.value = LoadingState.LOADING
            viewModelScope.launch {
                val addToFavorites = repository.addFavTitleToFirestore(currentUser()!!.uid, AddTitleToFirestore(
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

    fun removeTitleFromFirestore(titleId: Int) {
        traktFavoriteLoader.value = LoadingState.LOADING
        viewModelScope.launch {
            val removeFromFavorites = repository.removeFavTitleFromFirestore(currentUser()!!.uid, titleId)
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
                val checkTitle = repository.checkTitleInFirestore(currentUser()!!.uid, titleId)
                _addToFavorites.value = checkTitle!!.data != null
                traktFavoriteLoader.value = LoadingState.LOADED
            }
        } else {
            _addToFavorites.value = false
        }
    }
}