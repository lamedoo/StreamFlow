package com.lukakordzaia.streamflowphone.ui.phonesingletitle.tvepisodesbottomsheet

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lukakordzaia.core.utils.AppConstants
import com.lukakordzaia.core.baseclasses.BaseViewModel
import com.lukakordzaia.core.database.continuewatchingdb.ContinueWatchingRoom
import com.lukakordzaia.core.domain.domainmodels.TitleEpisodes
import com.lukakordzaia.core.network.LoadingState
import com.lukakordzaia.core.network.ResultData
import kotlinx.coroutines.launch

class TvEpisodeBottomSheetViewModel : BaseViewModel() {
    private val _movieNotYetAdded = MutableLiveData<Boolean>()
    val movieNotYetAdded: LiveData<Boolean> = _movieNotYetAdded

    private val _availableLanguages = MutableLiveData<MutableList<String>>()
    val availableLanguages: LiveData<MutableList<String>> = _availableLanguages

    private val _chosenSeason = MutableLiveData<Int>()
    val chosenSeason: LiveData<Int> = _chosenSeason

    private val _episodeInfo = MutableLiveData<List<TitleEpisodes>>()
    val episodeInfo: LiveData<List<TitleEpisodes>> = _episodeInfo

    private val _continueWatchingDetails = MediatorLiveData<ContinueWatchingRoom?>()
    val continueWatchingDetails: LiveData<ContinueWatchingRoom?> = _continueWatchingDetails

    fun checkAuthDatabase(titleId: Int) {
        if (sharedPreferences.getLoginToken() == "") {
            getSingleContinueWatchingFromRoom(titleId)
        } else {
            getContinueWatching(titleId)
        }
    }

    private fun getSingleContinueWatchingFromRoom(titleId: Int){
        val data = environment.databaseRepository.getSingleContinueWatchingFromRoom(titleId)

        _continueWatchingDetails.addSource(data) {
            _continueWatchingDetails.value = it
        }
    }

    private fun getContinueWatching(titleId: Int) {
        viewModelScope.launch {
            when (val titleData = environment.singleTitleRepository.getSingleTitleData(titleId)) {
                is ResultData.Success -> {
                    val data = titleData.data

                    if (data.data.userWatch?.data?.season != null) {
                        _continueWatchingDetails.postValue(
                            ContinueWatchingRoom(
                                titleId = titleId,
                                language = data.data.userWatch!!.data?.language!!,
                                watchedDuration = data.data.userWatch!!.data?.progress!!,
                                titleDuration = data.data.userWatch!!.data?.duration!!,
                                isTvShow = data.data.isTvShow,
                                season = data.data.userWatch!!.data?.season!!,
                                episode = data.data.userWatch!!.data?.episode!!
                            )
                        )
                    } else {
                        _continueWatchingDetails.postValue(null)
                    }
                }
                is ResultData.Error -> {
                    newToastMessage("ინფორმაცია - ${titleData.exception}")
                }
                is ResultData.Internet -> {
                    setNoInternet()
                }
            }
        }
    }

    fun getSeasonFiles(titleId: Int, season: Int) {
        _episodeInfo.value = emptyList()
        setGeneralLoader(LoadingState.LOADING)
        _chosenSeason.value = season
        viewModelScope.launch {
            when (val files = environment.singleTitleRepository.getSingleTitleFiles(titleId, season)) {
                is ResultData.Success -> {
                    val data = files.data.data
                    if (data.isNotEmpty()) {
                        val getEpisodeNames: MutableList<TitleEpisodes> = ArrayList()

                        if (sharedPreferences.getLoginToken() == "") {
                            data.forEach {
                                getEpisodeNames.add(TitleEpisodes(titleId, it.episode, it.title, it.covers.x1050!!))
                            }
                        } else {
                            data.forEach {
                                getEpisodeNames.add(TitleEpisodes(
                                    titleId,
                                    it.episode,
                                    it.title,
                                    it.covers.x1050!!,
                                    it.userWatch.duration,
                                    it.userWatch.progress
                                ))
                            }
                        }
                        _episodeInfo.value = getEpisodeNames

                        _movieNotYetAdded.value = false
                    } else {
                        _movieNotYetAdded.value = true
                    }
                    setGeneralLoader(LoadingState.LOADED)
                }
                is ResultData.Error -> {
                    when (files.exception) {
                        AppConstants.UNKNOWN_ERROR -> {
                            _movieNotYetAdded.value = true
                        }
                        else -> {
                            newToastMessage(files.exception)
                        }
                    }
                }
                is ResultData.Internet -> {
                    setNoInternet()
                }
            }
        }
    }

    fun getEpisodeLanguages(titleId: Int, episodeNum: Int) {
        viewModelScope.launch {
            when (val files = environment.singleTitleRepository.getSingleTitleFiles(titleId, chosenSeason.value!!)) {
                is ResultData.Success -> {
                    val episode = files.data.data[episodeNum]

                    val fetchLanguages: MutableList<String> = ArrayList()
                    episode.files.forEach {
                        fetchLanguages.add(it.lang)
                    }
                    _availableLanguages.value = fetchLanguages
                }
            }
        }
    }
}