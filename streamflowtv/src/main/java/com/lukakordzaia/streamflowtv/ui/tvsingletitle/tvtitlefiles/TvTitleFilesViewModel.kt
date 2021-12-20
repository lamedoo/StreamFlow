package com.lukakordzaia.streamflowtv.ui.tvsingletitle.tvtitlefiles

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lukakordzaia.core.baseclasses.BaseViewModel
import com.lukakordzaia.core.database.continuewatchingdb.ContinueWatchingRoom
import com.lukakordzaia.core.datamodels.SingleTitleModel
import com.lukakordzaia.core.datamodels.TitleEpisodes
import com.lukakordzaia.core.network.Result
import com.lukakordzaia.core.network.models.imovies.response.singletitle.GetSingleTitleCastResponse
import com.lukakordzaia.core.network.toTitleListModel
import kotlinx.coroutines.launch

class TvTitleFilesViewModel : BaseViewModel() {
    private val _availableLanguages = MutableLiveData<MutableList<String>>()
    val availableLanguages: LiveData<MutableList<String>> = _availableLanguages

    private val _chosenLanguage = MutableLiveData<String>()
    val chosenLanguage: LiveData<String> = _chosenLanguage

    private val _chosenSeason = MutableLiveData<Int>()
    val chosenSeason: LiveData<Int> = _chosenSeason

    private val _episodeNames = MutableLiveData<List<TitleEpisodes>>()
    val episodeNames: LiveData<List<TitleEpisodes>> = _episodeNames

    private val _numOfSeasons = MutableLiveData<Int>()
    val numOfSeasons: LiveData<Int> = _numOfSeasons

    private val _castData = MutableLiveData<List<GetSingleTitleCastResponse.Data>>()
    val castResponseDataGetSingle: LiveData<List<GetSingleTitleCastResponse.Data>> = _castData

    private val _singleTitleRelated = MutableLiveData<List<SingleTitleModel>>()
    val singleTitleRelated: LiveData<List<SingleTitleModel>> = _singleTitleRelated

    private val _continueWatchingDetails = MediatorLiveData<ContinueWatchingRoom?>()
    val continueWatchingDetails: LiveData<ContinueWatchingRoom?> = _continueWatchingDetails

    fun getSingleTitleData(titleId: Int) {
        viewModelScope.launch {
            when (val data = environment.singleTitleRepository.getSingleTitleData(titleId)) {
                is Result.Success -> {
                    if (data.data.data.seasons != null) {
                        _numOfSeasons.value = data.data.data.seasons!!.data.size

                        if (sharedPreferences.getLoginToken() == "") {
                            getSingleContinueWatchingFromRoom(titleId)
                        } else {
                            if (data.data.data.userWatch?.data?.season != null) {
                                _continueWatchingDetails.value = ContinueWatchingRoom(
                                    titleId = titleId,
                                    language = data.data.data.userWatch!!.data?.language!!,
                                    watchedDuration = data.data.data.userWatch!!.data?.progress!!,
                                    titleDuration = data.data.data.userWatch!!.data?.duration!!,
                                    isTvShow = data.data.data.isTvShow,
                                    season = data.data.data.userWatch!!.data?.season!!,
                                    episode = data.data.data.userWatch!!.data?.episode!!
                                )
                            } else {
                                _continueWatchingDetails.value = null
                            }
                        }
                    }
                }
                is Result.Error -> {
                    newToastMessage(data.exception)
                }
            }
        }
    }

    fun getSeasonFiles(titleId: Int, season: Int) {
        _chosenSeason.value = season
        viewModelScope.launch {
            when (val files = environment.singleTitleRepository.getSingleTitleFiles(titleId, season)) {
                is Result.Success -> {
                    val data = files.data.data

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
                    _episodeNames.value = getEpisodeNames
                }
                is Result.Error -> {
                    newToastMessage("ეპიზოდები - ${files.exception}")
                }
            }
        }
    }

    fun getEpisodeLanguages(titleId: Int, episodeNum: Int) {
        viewModelScope.launch {
            when (val files = environment.singleTitleRepository.getSingleTitleFiles(titleId, chosenSeason.value!!)) {
                is Result.Success -> {
                    val episode = files.data.data[episodeNum-1]

                    val fetchLanguages: MutableList<String> = ArrayList()
                    episode.files.forEach {
                        fetchLanguages.add(it.lang)
                    }
                    _availableLanguages.value = fetchLanguages
                }
            }
        }
    }

    fun getSingleTitleCast(titleId: Int) {
        viewModelScope.launch {
            when (val cast = environment.singleTitleRepository.getSingleTitleCast(titleId, "cast")) {
                is Result.Success -> {
                    val data = cast.data.data

                    _castData.value = data
                }
                is Result.Error -> {
                    newToastMessage("მსახიობები - ${cast.exception}")
                }
            }
        }
    }

    fun getSingleTitleRelated(titleId: Int) {
        viewModelScope.launch {
            when (val related = environment.singleTitleRepository.getSingleTitleRelated(titleId)) {
                is Result.Success -> {
                    val data = related.data.data
                    _singleTitleRelated.value = data.toTitleListModel()
                }
                is Result.Error -> {
                    newToastMessage("მსგავსი - ${related.exception}")
                }
            }
        }
    }

    private fun getSingleContinueWatchingFromRoom(titleId: Int){
        val data = environment.databaseRepository.getSingleContinueWatchingFromRoom(titleId)

        _continueWatchingDetails.addSource(data) {
            _continueWatchingDetails.value = it
        }
    }
}