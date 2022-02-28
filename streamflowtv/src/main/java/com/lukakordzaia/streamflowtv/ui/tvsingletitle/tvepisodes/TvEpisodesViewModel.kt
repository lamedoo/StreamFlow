package com.lukakordzaia.streamflowtv.ui.tvsingletitle.tvepisodes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lukakordzaia.core.baseclasses.BaseViewModel
import com.lukakordzaia.core.database.continuewatchingdb.ContinueWatchingRoom
import com.lukakordzaia.core.domain.domainmodels.SingleTitleModel
import com.lukakordzaia.core.domain.domainmodels.SeasonEpisodesModel
import com.lukakordzaia.core.domain.usecases.DbSingleContinueWatchingUseCase
import com.lukakordzaia.core.domain.usecases.SingleTitleFilesUseCase
import com.lukakordzaia.core.domain.usecases.SingleTitleUseCase
import com.lukakordzaia.core.network.ResultDomain
import com.lukakordzaia.core.utils.AppConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TvEpisodesViewModel(
    private val dbSingleContinueWatchingUseCase: DbSingleContinueWatchingUseCase,
    private val singleTitleUseCase: SingleTitleUseCase,
    private val singleTitleFilesUseCase: SingleTitleFilesUseCase
) : BaseViewModel() {
    private val _singleTitleData = MutableLiveData<SingleTitleModel>()
    val getSingleTitleResponse: LiveData<SingleTitleModel> = _singleTitleData

    private val _chosenSeason = MutableLiveData<Int>()
    val chosenSeason: LiveData<Int> = _chosenSeason

    private val _seasonEpisodes = MutableLiveData<List<SeasonEpisodesModel>>()
    val seasonEpisodes: LiveData<List<SeasonEpisodesModel>> = _seasonEpisodes

    private val _numOfSeasons = MutableLiveData<Int>()
    val numOfSeasons: LiveData<Int> = _numOfSeasons

    private val _continueWatchingDetails = MediatorLiveData<ContinueWatchingRoom?>()
    val continueWatchingDetails: LiveData<ContinueWatchingRoom?> = _continueWatchingDetails

    fun getContinueWatching(titleId: Int) {
        viewModelScope.launch {
            when (val result = singleTitleUseCase.invoke(titleId)) {
                is ResultDomain.Success -> {
                    val data = result.data

                    _singleTitleData.value = data

                    if (sharedPreferences.getLoginToken() == "") {
                        getSingleContinueWatchingFromRoom(titleId)
                    } else {
                        if (data.currentSeason != null) {
                            _continueWatchingDetails.value = ContinueWatchingRoom(
                                titleId = titleId,
                                language = data.currentLanguage!!,
                                watchedDuration = data.watchedDuration!!,
                                titleDuration = data.titleDuration!!,
                                isTvShow = data.isTvShow,
                                season = data.currentSeason!!,
                                episode = data.currentEpisode!!
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

    fun getNumOfSeasons(titleId: Int) {
        viewModelScope.launch {
            when (val result = singleTitleUseCase.invoke(titleId)) {
                is ResultDomain.Success -> {
                    _numOfSeasons.value = result.data.seasonNum ?: 1
                }
                is ResultDomain.Error -> {
                    when (result.exception) {
                        AppConstants.NO_INTERNET_ERROR -> {}
                        else -> newToastMessage("სეზონების რაოდენობა - ${result.exception}")
                    }
                }
            }
        }
    }

    fun getSeasonFiles(titleId: Int, season: Int) {
        _chosenSeason.value = season
        viewModelScope.launch {
            when (val result = singleTitleFilesUseCase.invoke(Pair(titleId, season))) {
                is ResultDomain.Success -> {
                    val data = result.data

                    val getEpisodeNames: MutableList<SeasonEpisodesModel> = ArrayList()
                    if (sharedPreferences.getLoginToken() == "") {
                        data.forEach {
                            getEpisodeNames.add(SeasonEpisodesModel(it.episode, it.title, it.cover!!, it.languages))
                        }
                    } else {
                        data.forEach {
                            getEpisodeNames.add(
                                SeasonEpisodesModel(
                                    it.episode,
                                    it.title,
                                    it.cover!!,
                                    it.languages,
                                    it.titleDuration,
                                    it.watchedDuration
                                )
                            )
                        }
                    }
                    _seasonEpisodes.value = getEpisodeNames
                }
                is ResultDomain.Error -> {
                    when (result.exception) {
                        AppConstants.NO_INTERNET_ERROR -> setNoInternet()
                        else -> newToastMessage("ეპიზოდები - ${result.exception}")
                    }
                }
            }
        }
    }

    private fun getSingleContinueWatchingFromRoom(titleId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _continueWatchingDetails.addSource(dbSingleContinueWatchingUseCase.invoke(titleId)) {
                _continueWatchingDetails.value = it
            }
        }
    }
}