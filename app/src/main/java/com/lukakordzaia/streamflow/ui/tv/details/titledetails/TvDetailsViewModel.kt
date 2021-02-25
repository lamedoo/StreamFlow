package com.lukakordzaia.streamflow.ui.tv.details.titledetails

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lukakordzaia.streamflow.database.DbDetails
import com.lukakordzaia.streamflow.database.ImoviesDatabase
import com.lukakordzaia.streamflow.datamodels.SingleTitleData
import com.lukakordzaia.streamflow.network.FirebaseContinueWatchingCallBack
import com.lukakordzaia.streamflow.network.LoadingState
import com.lukakordzaia.streamflow.network.Result
import com.lukakordzaia.streamflow.repository.TvDetailsRepository
import com.lukakordzaia.streamflow.ui.baseclasses.BaseViewModel
import com.lukakordzaia.streamflow.utils.AppConstants
import kotlinx.coroutines.launch

class TvDetailsViewModel(private val repository: TvDetailsRepository) : BaseViewModel() {
    private val _singleTitleData = MutableLiveData<SingleTitleData.Data>()
    val singleTitleData: LiveData<SingleTitleData.Data> = _singleTitleData

    private val _movieNotYetAdded = MutableLiveData<Boolean>()
    val movieNotYetAdded: LiveData<Boolean> = _movieNotYetAdded

    private val _dataLoader = MutableLiveData<LoadingState>()
    val dataLoader: LiveData<LoadingState> = _dataLoader

    private val _availableLanguages = MutableLiveData<MutableList<String>>()
    val availableLanguages: LiveData<MutableList<String>> = _availableLanguages

    private val _chosenLanguage = MutableLiveData<String>()
    val chosenLanguage: LiveData<String> = _chosenLanguage

    private val _continueWatchingDetails = MutableLiveData<DbDetails>()
    val continueWatchingDetails: LiveData<DbDetails> = _continueWatchingDetails

    fun getSingleTitleData(titleId: Int) {
        viewModelScope.launch {
            _dataLoader.value = LoadingState.LOADING
            when (val data = repository.getSingleTitleData(titleId)) {
                is Result.Success -> {
                    _singleTitleData.value = data.data.data

                    _dataLoader.value = LoadingState.LOADED
                }
                is Result.Error -> {
                    newToastMessage(data.exception)
                }
                is Result.Internet -> {
                    setNoInternet()
                }
            }
        }
    }

    fun checkContinueWatchingTitleInRoom(context: Context, titleId: Int): LiveData<Boolean> {
        val database = ImoviesDatabase.getDatabase(context)?.getDao()
        return repository.checkContinueWatchingTitleInRoom(database!!, titleId)
    }

    fun getSingleContinueWatchingFromRoom(context: Context, titleId: Int){
        viewModelScope.launch {
            _continueWatchingDetails.value = repository.getSingleContinueWatchingFromRoom(roomDb(context)!!, titleId)
        }
    }

    fun deleteSingleContinueWatchingFromRoom(context: Context, titleId: Int) {
        val database = ImoviesDatabase.getDatabase(context)?.getDao()
        viewModelScope.launch {
            repository.deleteSingleContinueWatchingFromRoom(database!!, titleId)
        }
    }

    fun checkContinueWatchingInFirestore(titleId: Int) {
        repository.checkContinueWatchingInFirestore(currentUser()!!.uid, titleId, object : FirebaseContinueWatchingCallBack {
            override fun continueWatchingTitle(title: DbDetails) {
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

    fun getTitleLanguageFiles(language: String) {
        _chosenLanguage.value = language
    }
}