package com.lukakordzaia.streamflowtv.ui.tvsingletitle.tvepisodes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lukakordzaia.core.baseclasses.BaseViewModel
import com.lukakordzaia.core.database.continuewatchingdb.ContinueWatchingRoom
import com.lukakordzaia.core.datamodels.SingleTitleModel
import com.lukakordzaia.core.datamodels.TitleEpisodes
import com.lukakordzaia.core.network.ResultData
import com.lukakordzaia.core.network.toSingleTitleModel
import kotlinx.coroutines.launch

class TvEpisodesViewModel : BaseViewModel() {
    private val _singleTitleData = MutableLiveData<SingleTitleModel>()
    val getSingleTitleResponse: LiveData<SingleTitleModel> = _singleTitleData

    private val _availableLanguages = MutableLiveData<MutableList<String>>()
    val availableLanguages: LiveData<MutableList<String>> = _availableLanguages

    private val _chosenLanguage = MutableLiveData<String>()
    val chosenLanguage: LiveData<String> = _chosenLanguage

    private val _chosenSeason = MutableLiveData<Int>()
    val chosenSeason: LiveData<Int> = _chosenSeason

    private val _chosenEpisode = MutableLiveData<Int>()
    val chosenEpisode: LiveData<Int> = _chosenEpisode

    private val _episodeNames = MutableLiveData<List<TitleEpisodes>>()
    val episodeNames: LiveData<List<TitleEpisodes>> = _episodeNames

    private val _numOfSeasons = MutableLiveData<Int>()
    val numOfSeasons: LiveData<Int> = _numOfSeasons

    private val _continueWatchingDetails = MediatorLiveData<ContinueWatchingRoom?>()
    val continueWatchingDetails: LiveData<ContinueWatchingRoom?> = _continueWatchingDetails

    fun getSingleTitleData(titleId: Int) {
        viewModelScope.launch {
            when (val data = environment.singleTitleRepository.getSingleTitleData(titleId)) {
                is ResultData.Success -> {
                    _singleTitleData.value = data.data.toSingleTitleModel()
                }
                is ResultData.Error -> {
                    newToastMessage(data.exception)
                }
            }
        }
    }

    fun getContinueWatching(titleId: Int) {
        viewModelScope.launch {
            when (val info = environment.singleTitleRepository.getSingleTitleData(titleId)) {
                is ResultData.Success -> {
                    val data = info.data

                    if (sharedPreferences.getLoginToken() == "") {
                        getSingleContinueWatchingFromRoom(titleId)
                    } else {
                        if (data.data.userWatch?.data?.season != null) {
                            _continueWatchingDetails.value = ContinueWatchingRoom(
                                titleId = titleId,
                                language = data.data.userWatch!!.data?.language!!,
                                watchedDuration = data.data.userWatch!!.data?.progress!!,
                                titleDuration = data.data.userWatch!!.data?.duration!!,
                                isTvShow = data.data.isTvShow,
                                season = data.data.userWatch!!.data?.season!!,
                                episode = data.data.userWatch!!.data?.episode!!
                            )
                        } else {
                            _continueWatchingDetails.value = null
                        }
                    }
                }
                is ResultData.Error -> {
                    newToastMessage(info.exception)
                }
                is ResultData.Internet -> {
                    setNoInternet()
                }
            }
        }
    }

    fun getNumOfSeasons(titleId: Int) {
        viewModelScope.launch {
            when (val data = environment.singleTitleRepository.getSingleTitleData(titleId)) {
                is ResultData.Success -> {
                    if (data.data.data.seasons != null) {
                        _numOfSeasons.value = data.data.data.seasons!!.data.size
                    }
                }
                is ResultData.Error -> {
                    newToastMessage(data.exception)
                }
            }
        }
    }

    fun getSeasonFiles(titleId: Int, season: Int) {
        _chosenSeason.value = season
        viewModelScope.launch {
            when (val files = environment.singleTitleRepository.getSingleTitleFiles(titleId, season)) {
                is ResultData.Success -> {
                    val data = files.data.data

                    val getEpisodeNames: MutableList<TitleEpisodes> = ArrayList()
                    if (sharedPreferences.getLoginToken() == "") {
                        data.forEach {
                            getEpisodeNames.add(TitleEpisodes(titleId, it.episode, it.title, it.covers.x1050!!))
                        }
                    } else {
                        data.forEach {
                            getEpisodeNames.add(
                                TitleEpisodes(
                                titleId,
                                    it.episode,
                                    it.title,
                                    it.covers.x1050!!,
                                    it.userWatch.duration,
                                    it.userWatch.progress
                                )
                            )
                        }
                    }
                    _episodeNames.value = getEpisodeNames
                }
                is ResultData.Error -> {
                    newToastMessage("ეპიზოდები - ${files.exception}")
                }
            }
        }
    }

    fun getEpisodeLanguages(titleId: Int, episodeNum: Int) {
        viewModelScope.launch {
            when (val files = environment.singleTitleRepository.getSingleTitleFiles(titleId, chosenSeason.value!!)) {
                is ResultData.Success -> {
                    val episode = files.data.data[episodeNum - 1]

                    val fetchLanguages: MutableList<String> = ArrayList()
                    episode.files.forEach {
                        fetchLanguages.add(it.lang)
                    }
                    _availableLanguages.value = fetchLanguages
                }
            }
        }
    }

    private fun getSingleContinueWatchingFromRoom(titleId: Int) {
        val data = environment.databaseRepository.getSingleContinueWatchingFromRoom(titleId)

        _continueWatchingDetails.addSource(data) {
            _continueWatchingDetails.value = it
        }
    }

    fun setChosenSeason(season: Int) {
        _chosenSeason.value = season
    }

    fun setChoseEpisode(episode: Int) {
        _chosenEpisode.value = episode
    }
}