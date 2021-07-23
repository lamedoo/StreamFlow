package com.lukakordzaia.streamflow.ui.phone.singletitle.choosetitledetails

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lukakordzaia.streamflow.database.continuewatchingdb.ContinueWatchingRoom
import com.lukakordzaia.streamflow.datamodels.TitleEpisodes
import com.lukakordzaia.streamflow.network.FirebaseContinueWatchingListCallBack
import com.lukakordzaia.streamflow.network.LoadingState
import com.lukakordzaia.streamflow.network.Result
import com.lukakordzaia.streamflow.repository.SingleTitleRepository
import com.lukakordzaia.streamflow.ui.baseclasses.BaseViewModel
import com.lukakordzaia.streamflow.utils.AppConstants
import kotlinx.coroutines.launch

class ChooseTitleDetailsViewModel(private val repository: SingleTitleRepository) : BaseViewModel() {
    val chooseDetailsLoader = MutableLiveData<LoadingState>()

    private val _movieNotYetAdded = MutableLiveData<Boolean>()
    val movieNotYetAdded: LiveData<Boolean> = _movieNotYetAdded

    private val _availableLanguages = MutableLiveData<MutableList<String>>()
    val availableLanguages: LiveData<MutableList<String>> = _availableLanguages

    private val _chosenLanguage = MutableLiveData<String>()
    val chosenLanguage: LiveData<String> = _chosenLanguage

    private val _chosenSeason = MutableLiveData<Int>()
    val chosenSeason: LiveData<Int> = _chosenSeason

    private val _episodeInfo = MutableLiveData<List<TitleEpisodes>>()
    val episodeInfo: LiveData<List<TitleEpisodes>> = _episodeInfo

    private val _continueWatchingDetails = MutableLiveData<ContinueWatchingRoom>(null)
    val     continueWatchingDetails: LiveData<ContinueWatchingRoom> = _continueWatchingDetails

    fun checkContinueWatchingTitleInRoom(context: Context, titleId: Int): LiveData<Boolean> {
        return repository.checkContinueWatchingTitleInRoom(roomDb(context)!!, titleId)
    }

    fun getSingleContinueWatchingFromRoom(context: Context, titleId: Int){
        viewModelScope.launch {
            _continueWatchingDetails.value = repository.getSingleContinueWatchingFromRoom(roomDb(context)!!, titleId)
        }
    }

    fun checkContinueWatchingInFirestore1(titleId: Int) {
        repository.checkContinueWatchingInFirestore1(currentUser()!!.uid, titleId, object : FirebaseContinueWatchingListCallBack {
            override fun continueWatchingList(titleList: MutableList<ContinueWatchingRoom>) {
                _continueWatchingDetails.value = titleList[0]
            }
        })
    }

    fun checkContinueWatchingInFirestore(titleId: Int) {
        viewModelScope.launch {
            val checkContinueWatching = repository.checkContinueWatchingInFirestore(currentUser()!!.uid, titleId)

            if (checkContinueWatching!!.data != null) {
                _continueWatchingDetails.value = ContinueWatchingRoom(
                        checkContinueWatching.data!!["id"].toString().toInt(),
                        checkContinueWatching.data!!["language"].toString(),
                        checkContinueWatching.data!!["continueFrom"] as Long,
                        checkContinueWatching.data!!["titleDuration"] as Long,
                        checkContinueWatching.data!!["isTvShow"] as Boolean,
                        checkContinueWatching.data!!["season"].toString().toInt(),
                        checkContinueWatching.data!!["episode"].toString().toInt()
                )
            }
        }
    }

    fun getSeasonFiles(titleId: Int, season: Int) {
        _chosenSeason.value = season
        viewModelScope.launch {
            chooseDetailsLoader.value = LoadingState.LOADING
            when (val files = repository.getSingleTitleFiles(titleId, season)) {
                is Result.Success -> {
                    val data = files.data.data
                    if (data.isNotEmpty()) {
                        val fetchLanguages: MutableList<String> = ArrayList()

                        data[0].files.forEach {
                            fetchLanguages.add(it.lang)
                        }
                        _availableLanguages.value = fetchLanguages

                        val getEpisodeNames: MutableList<TitleEpisodes> = ArrayList()
                        data.forEach {
                            getEpisodeNames.add(TitleEpisodes(it.episode, it.title, it.covers.x1050!!))
                        }
                        _episodeInfo.value = getEpisodeNames

                        _movieNotYetAdded.value = false
                    } else {
                        _movieNotYetAdded.value = true
                    }
                    chooseDetailsLoader.value = LoadingState.LOADED
                }
                is Result.Error -> {
                    when (files.exception) {
                        AppConstants.UNKNOWN_ERROR -> {
                            _movieNotYetAdded.value = true
                        }
                        else -> {
                            newToastMessage(files.exception)
                        }
                    }
                }
                is Result.Internet -> {
                    setNoInternet()
                }
            }
        }
    }
}